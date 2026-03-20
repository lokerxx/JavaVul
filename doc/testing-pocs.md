# 支持测试接口清单

这份文档专门说明仓库里“可直接回放”的测试接口来源、使用方式和覆盖范围。

当前测试清单不再以内嵌表格维护在 `README.md` 里，而是以这里和 `python_scripts/poc/` 目录为准。

## 清单来源

所有可回放接口定义都在下面这些文件里：

- `python_scripts/poc/actuator.py`
- `python_scripts/poc/base_vul.py`
- `python_scripts/poc/collections.py`
- `python_scripts/poc/data_access.py`
- `python_scripts/poc/druid.py`
- `python_scripts/poc/fastjson.py`
- `python_scripts/poc/integration.py`
- `python_scripts/poc/log4j.py`
- `python_scripts/poc/logic_vul.py`
- `python_scripts/poc/shiro.py`
- `python_scripts/poc/struts.py`

统一加载入口在：

- `python_scripts/poc/__init__.py`

统一回放脚本在：

- `python_scripts/replay_all.py`

## 当前覆盖规模

按当前 `python_scripts/poc` 配置统计：

- 总接口数：`245`
- 攻击流量：`113`
- 正常流量：`84`
- 修复对照：`47`
- 误报样例：`1`

## 使用方式

最常用的回放命令：

```bash
python3 python_scripts/replay_all.py --host 127.0.0.1 --mode attack
```

如果要同时带代理重放：

```bash
python3 python_scripts/replay_all.py --host 127.0.0.1 --mode attack --proxy http://127.0.0.1:8080
```

如果只看某一类项目：

```bash
python3 python_scripts/replay_all.py --host 127.0.0.1 --mode all --contains struts
python3 python_scripts/replay_all.py --host 127.0.0.1 --mode all --contains fastjson
```

## PoC 模块分布

| 模块文件 | 覆盖范围 |
| :-- | :-- |
| `python_scripts/poc/actuator.py` | Spring Boot Actuator 1.X / 2.X 未授权与修复对照 |
| `python_scripts/poc/base_vul.py` | SQL 注入、XSS、文件读写、SSRF、SSTI、XXE、命令执行、重定向、ReDoS 等基础漏洞 |
| `python_scripts/poc/collections.py` | Commons Collections 反序列化靶场 |
| `python_scripts/poc/data_access.py` | HSQLDB、Hibernate 注入相关接口 |
| `python_scripts/poc/druid.py` | Druid 未授权与 SQLWall 误报样例 |
| `python_scripts/poc/fastjson.py` | 多版本 Fastjson 反序列化利用与基线流量 |
| `python_scripts/poc/integration.py` | 微信支付 XXE、XStream、Jackson-databind、CAS XXE |
| `python_scripts/poc/log4j.py` | Log4Shell 攻击与正常流量 |
| `python_scripts/poc/logic_vul.py` | 业务逻辑漏洞综合靶场 |
| `python_scripts/poc/shiro.py` | 多版本 Shiro RememberMe、Padding Oracle、认证绕过 |
| `python_scripts/poc/struts.py` | Struts2 S2-001 到 S2-062 当前已接入的靶场接口 |

## 维护原则

1. 新增靶场后，优先把对应接口补到 `python_scripts/poc/*.py`。
2. 端口要和根目录 `docker-compose-local.yaml` 保持一致。
3. 如果某个项目支持正常流量、攻击流量、修复流量，尽量三类都补。
4. 需要特殊请求体的项目，直接在 PoC 里保留原始 `headers` 和 `data`，不要只写说明不落配置。

## 相关文档

- 文档索引：[`doc/README.md`](./README.md)
- 项目操作教程：[`doc/project-tutorials.md`](./project-tutorials.md)
- 根项目说明：[`README.md`](../README.md)
