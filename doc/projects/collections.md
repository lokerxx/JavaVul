# collections 操作教程

- 类型：单体靶场
- 目录：`collections`
- 端口：`9945`
- 推荐入口：`/playground`

## 这是什么

Commons Collections 反序列化演示靶场，当前已经接入统一 compose，可以直接通过页面按钮或回放脚本触发。
除了直接访问 `/transformer` 之外，项目还提供了：

- `GET /payload?command=...`：生成序列化字节流
- `POST /deserialize`：接收外部上传的序列化字节流并触发反序列化
- `GET /status`：查看 `/tmp/collections-success` 和 `/tmp/collections-output` 状态

## 具体操作步骤

1. 运行统一编排，或单独在项目目录执行 `docker compose up --build`。
2. 打开 `http://宿主机IP:9945/playground`，优先点页面里的 `touch 标记`。
3. 再点 `查看状态`，确认 `/tmp/collections-success` 已存在。
4. 如果你想看命令输出，可以填充 `id > /tmp/collections-output`，然后再访问 `http://宿主机IP:9945/status`。
5. 页面里的“上传字节流触发”会先请求 `/payload`，再把字节流 POST 到 `/deserialize`，更接近真实外部输入场景。
6. 如果你要统一回放，直接使用 `collections_attack_touch` 或 `collections_attack_output`。

## 相关入口

- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
