#!/usr/bin/env python3
import argparse
import json
import os
import sys
import time
from pathlib import Path

import requests

DEFAULT_PROXIES = {}
DEFAULT_TIMEOUT = 15
VALID_MODES = {"attack", "normal", "mistake", "mistak", "repair", "all"}
MODE_ALIAS = {"mistak": "mistake"}


def normalize_modes(raw_modes):
    # 统一处理模式别名，并在传入 all 时展开成完整模式集合。
    normalized = []
    for mode in raw_modes:
        mode = MODE_ALIAS.get(mode, mode)
        if mode not in VALID_MODES:
            raise ValueError("unsupported mode: {}".format(mode))
        normalized.append(mode)
    if "all" in normalized:
        return {"attack", "normal", "mistake", "repair"}
    return set(normalized)


def build_proxies(args):
    # 支持统一代理，也支持分别设置 http / https 代理。
    if args.proxy:
        return {"http": args.proxy, "https": args.proxy}

    proxies = dict(DEFAULT_PROXIES)
    if args.http_proxy:
        proxies["http"] = args.http_proxy
    if args.https_proxy:
        proxies["https"] = args.https_proxy
    return proxies


def should_use_item(item_type, selected_modes):
    # PoC 定义里允许使用 mistak 旧拼写，这里统一按标准模式判断。
    item_type = MODE_ALIAS.get(item_type, item_type)
    return item_type in selected_modes


def build_request_kwargs(config, proxies, timeout, root_dir):
    # 把 PoC 配置转换成 requests.request 可直接使用的参数。
    kwargs = {
        "url": config["url"],
        "headers": config.get("headers", {}),
        "timeout": timeout,
        "proxies": proxies,
    }

    if "data" in config and config["method"].upper() != "GET":
        kwargs["data"] = config["data"]

    if "file" in config:
        # 文件上传场景支持相对路径，默认相对仓库根目录解析。
        file_path = Path(config["file"])
        if not file_path.is_absolute():
            file_path = root_dir / file_path
        if not file_path.exists():
            raise FileNotFoundError("file not found: {}".format(file_path))
        param_name = config.get("parm", "file")
        kwargs["files"] = {
            param_name: (file_path.name, file_path.open("rb"))
        }

    return kwargs


def close_file_handles(kwargs):
    # requests 不会帮我们关闭手动打开的文件句柄，这里做统一回收。
    files = kwargs.get("files")
    if not files:
        return
    for _, file_tuple in files.items():
        if len(file_tuple) > 1 and hasattr(file_tuple[1], "close"):
            file_tuple[1].close()


def replay_one(api_name, config, session, proxies, timeout, root_dir, body_preview):
    # 执行单条请求，并尽量把响应和异常都整理成统一结构。
    method = config["method"].upper()
    started = time.time()
    kwargs = build_request_kwargs(config, proxies, timeout, root_dir)
    try:
        response = session.request(method=method, **kwargs)
        elapsed = time.time() - started
        text = response.text
        preview = text[:body_preview]
        return {
            "api": api_name,
            "name": config.get("name", api_name),
            "type": config.get("type", ""),
            "method": method,
            "url": config["url"],
            "status_code": response.status_code,
            "elapsed_ms": int(elapsed * 1000),
            "ok": response.ok,
            "headers": dict(response.headers),
            "body_preview": preview,
            "body_length": len(text),
            "error": "",
        }
    except Exception as exc:
        elapsed = time.time() - started
        return {
            "api": api_name,
            "name": config.get("name", api_name),
            "type": config.get("type", ""),
            "method": method,
            "url": config["url"],
            "status_code": 0,
            "elapsed_ms": int(elapsed * 1000),
            "ok": False,
            "headers": {},
            "body_preview": "",
            "body_length": 0,
            "error": str(exc),
        }
    finally:
        close_file_handles(kwargs)


def print_summary(results):
    # 终端输出简要统计，便于快速看整体成功率和失败项。
    total = len(results)
    success = sum(1 for item in results if item["ok"])
    failed = total - success
    print("total={} success={} failed={}".format(total, success, failed))
    for item in results:
        status = item["status_code"] if item["status_code"] else "ERR"
        print("[{type}] {api} -> {status} {elapsed}ms".format(
            type=item["type"],
            api=item["api"],
            status=status,
            elapsed=item["elapsed_ms"],
        ))
        if item["error"]:
            print("  error: {}".format(item["error"]))


def main():
    parser = argparse.ArgumentParser(
        description="Replay all PoC requests without the Flask index project."
    )
    parser.add_argument(
        "--mode",
        action="append",
        default=["all"],
        help="Replay mode: attack, normal, mistake, mistak, repair, all. Repeatable.",
    )
    parser.add_argument(
        "--contains",
        default="",
        help="Only replay APIs whose key contains this substring.",
    )
    parser.add_argument(
        "--timeout",
        type=int,
        default=DEFAULT_TIMEOUT,
        help="Requests timeout in seconds.",
    )
    parser.add_argument(
        "--proxy",
        default="",
        help="Set both http and https proxy at once, e.g. http://127.0.0.1:8080",
    )
    parser.add_argument("--http-proxy", default="", help="HTTP proxy.")
    parser.add_argument("--https-proxy", default="", help="HTTPS proxy.")
    parser.add_argument(
        "--output",
        default="",
        help="Optional JSON file path for full replay results.",
    )
    parser.add_argument(
        "--body-preview",
        type=int,
        default=200,
        help="Keep this many response body characters in the output preview.",
    )
    parser.add_argument(
        "--host",
        default="",
        help="回放时指定目标主机，例如 127.0.0.1。",
    )

    args = parser.parse_args()

    if args.host:
        # 先把 host 注入环境变量，再导入 poc，确保各模块按目标主机生成 URL。
        os.environ["HOST"] = args.host

    from poc import requests_config

    selected_modes = normalize_modes(args.mode)
    proxies = build_proxies(args)
    # 上传文件等相对路径默认以仓库根目录为基准解析。
    root_dir = Path(__file__).resolve().parents[1]

    session = requests.Session()
    filtered = []
    for api_name in sorted(requests_config):
        config = requests_config[api_name]
        # 先按模式过滤，再按名称关键字过滤。
        if not should_use_item(config.get("type", ""), selected_modes):
            continue
        if args.contains and args.contains not in api_name:
            continue
        filtered.append((api_name, config))

    if not filtered:
        print("no matching requests found")
        return 0

    print("replaying {} requests".format(len(filtered)))
    print("modes={}".format(",".join(sorted(selected_modes))))
    print("proxies={}".format(json.dumps(proxies, ensure_ascii=False)))

    results = []
    for api_name, config in filtered:
        # 顺序重放当前筛选后的全部请求，便于后续挂代理观察流量。
        result = replay_one(
            api_name=api_name,
            config=config,
            session=session,
            proxies=proxies,
            timeout=args.timeout,
            root_dir=root_dir,
            body_preview=args.body_preview,
        )
        results.append(result)

    print_summary(results)

    if args.output:
        # 可选写入完整 JSON 结果，方便后续比对和留档。
        output_path = Path(args.output)
        if not output_path.is_absolute():
            output_path = Path.cwd() / output_path
        output_path.write_text(
            json.dumps(results, ensure_ascii=False, indent=2),
            encoding="utf-8",
        )
        print("results written to {}".format(output_path))

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
