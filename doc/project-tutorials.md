# JavaVul 项目操作教程

这份文档给每个项目提供最短可执行的操作路径，默认以 `docker-compose-local.yaml` 为准。

## 通用步骤

1. 单体靶场启动：`bash run-local-build.sh`
2. 打开总控台：`http://宿主机IP:5000/`
3. 如果只想测单个项目，也可以直接访问下面表里的端口和入口。

## 控制台与辅助项目

| 项目 | 目录 | 入口/命令 | 快速操作 |
| :-- | :-- | :-- | :-- |
| 总控台 | `index` | `http://宿主机IP:5000/` | 从首页筛选项目，点击“测试”或“重放数据包”。 |
| Java Agent 示例 | `SimpleAgent` | 参考 [`./projects/simpleagent.md`](./projects/simpleagent.md) | 生成后把 JAR 放到 `agent/agent.jar`，再重启靶场。 |

补充说明：

- 支持直接回放的接口清单已经拆到 [`./testing-pocs.md`](./testing-pocs.md)

## 单体靶场项目

| 项目 | 目录 | 端口 | 推荐入口 | 快速操作教程 |
| :-- | :-- | :-- | :-- | :-- |
| Fastjson 1.2.24 | `fastjson-1.2.24` | `9999` | `/fastjson-1.2.24` | 打开页面后提交表单，或直接 POST 到 `/fastjson1.2.24-process`。 |
| Fastjson 1.2.25-1.2.41 | `fastjson-1.2.25-1.2.41` | `9987` | `/fastjson-1.2.25` | 用首页的 `fastjson1_2_25_attack`、`fastjson1_2_41_attack` 分别验证两条链。 |
| Fastjson 1.2.42 | `fastjson-1.2.42` | `9986` | `/fastjson-1.2.42` | 直接重放 `fastjson1_2_42_attack`。 |
| Fastjson 1.2.43 | `fastjson-1.2.43` | `9985` | `/fastjson-1.2.43` | 直接重放 `fastjson1_2_43_attack`。 |
| Fastjson 1.2.45 | `fastjson-1.2.45` | `9984` | `/fastjson-1.2.45` | 直接重放 `fastjson1_2_45_attack`。 |
| Fastjson 1.2.59 | `fastjson-1.2.59` | `9983` | `/fastjson-1.2.59` | 用 `fastjson1_2_59_attack_1` 和 `_2` 对比两个 payload。 |
| Fastjson 1.2.60 | `fastjson-1.2.60` | `9982` | `/fastjson-1.2.60` | 用 `fastjson1_2_60_attack_1` 和 `_2` 做对比验证。 |
| Fastjson 1.2.61 | `fastjson-1.2.61` | `9981` | `/fastjson-1.2.61` | 依次回放 `fastjson1_2_61_attack_1`、`_2`。 |
| Fastjson 1.2.62 | `fastjson-1.2.62` | `9980` | `/fastjson-1.2.62` | 用两条攻击模板观察不同 gadget 路径。 |
| Fastjson 1.2.66 | `fastjson-1.2.66` | `9979` | `/fastjson-1.2.66` | 首页已经提供 6 条攻击模板，可批量回放。 |
| Fastjson 1.2.67 | `fastjson-1.2.67` | `9978` | `/fastjson-1.2.67` | 重放 `_attack_1` 和 `_attack_2` 看不同 Shiro/JNDI 链。 |
| Fastjson 1.2.68 | `fastjson-1.2.68` | `9977` | `/fastjson-1.2.68` | 用两个 Hikari payload 进行测试。 |
| Fastjson 1.2.80 | `fastjson-1.2.80` | `9976` | `/fastjson-1.2.80` | 先访问页面，再回放 `fastjson1_2_80_attack`。 |
| Fastjson 1.2.83 | `fastjson-1.2.83` | `9975` | `/fastjson-1.2.83` | 当前首页提供正常流量模板，适合做基线验证。 |
| Log4j2 | `log4jvul` | `9998` | `/log4j2` | POST `name=${jndi:...}` 到 `/log4j2`，或直接用首页模板。 |
| Druid 未授权 | `druid_unauthorized` | `9997` | `/druid` | 直接访问控制台入口，验证未授权访问。 |
| Druid 修复 | `druid_authorized` | `9996` | `/druid` | 与未授权版本做对照，验证修复效果。 |
| Actuator 未授权 2.X | `actuator_unauthorized_2.X` | `9995` | `/actuator` | 直接访问根 actuator 入口。 |
| Actuator 修复 2.X | `actuator_authorized_2.X` | `9994` | `/actuator` | 验证修复后响应差异。 |
| Actuator 未授权 1.X | `actuator_unauthorized_1.X` | `9993` | `/trace` | 直接访问 `/trace`。 |
| Actuator 修复 1.X | `actuator_authorized_1.X` | `9992` | `/trace` | 与漏洞版做对照。 |
| 基础漏洞集 | `base_vul` | `9991` | `/swagger-ui.html` | 通过首页筛选 `base_vul` 相关条目，测试 SQLi、XSS、SSRF、SSTI、XXE 等接口。 |
| 基础漏洞修复集 | `base_vul_repair` | `9990` | `/swagger-ui.html` | 用首页的 repair 模板逐条对照验证。 |
| HSQLDB | `HSQLDB` | `9989` | `/hsqldb?username=1'` | 分别访问 `/hsqldb` 与 `/hsqldb_repair`。 |
| Hibernate | `Hibernate` | `9988` | `/Hibernate_injection?username=...` | 用 README/首页里的注入 payload 验证漏洞与修复。 |
| 微信支付 XXE | `wxpay-xxe` | `9974` | `/wxpay-xxe` | 直接 POST 首页提供的 XML payload。 |
| XStream | `CVE-2019-10173` | `9973` | `/CVE-2019-10173` | POST 首页内置 XML payload。 |
| Jackson-databind | `CVE-2019-12384` | `9972` | `/CVE-2019-12384` | GET 触发内置 PoC。 |
| CAS XXE | `cas_xxe` | `9971` | `/xxe_cas` | 用首页的 `cas_xxe_attack` 或 `cas_xxe_normal` 对照。 |
| Shiro 1.2.4 | `shior-1.2.4` | `9970` | `/shiro-1.2.4` | 先访问页面，再点弱 key 检测，或 POST `/login` 做 RememberMe 登录验证。 |
| Shiro 1.2.5-1.4.1 | `shiro-1.25_1.42` | `9969` | `/shiro-1.25_1.42` | 先加载样本，再调用 `/oracle/probe`、`/oracle/sweep` 观察 Padding Oracle 差异。 |
| Shiro 1.8.0 | `shiro-1.8.0` | `9968` | `/shiro-1.8.0` | 先查看弱 key 状态，再 POST `/login` 验证 RememberMe 行为。 |
| Struts2 S2-015 | `struts2-s2-015` | `9958` | `/index.action` | 先测通配符 Action 名结果，再测 `param.action?message=%{7*7}` 的二次引用执行。 |
| Struts2 S2-013 | `struts2-s2-013` | `9959` | `/link.action` | 带上恶意 GET 参数访问，再观察页面里 `includeParams="all"` 生成链接时是否触发 OGNL。 |
| Struts2 S2-012 | `struts2-s2-012` | `9960` | `/index.action` | 用页面按钮把 payload 放进 `name`，提交 `redirect` 后观察响应里是否直接回显命令结果。 |
| Struts2 S2-009 | `struts2-s2-009` | `9961` | `/example5.action` | 让 `name` 进入上下文，再通过 `z[(name)('meh')]` 做二次求值。 |
| Struts2 S2-007 | `struts2-s2-007` | `9962` | `/user.action` | 往 `age` 填入恶意字符串并触发类型转换错误，观察错误流程中的 OGNL 执行。 |
| Struts2 S2-005 | `struts2-s2-005` | `9963` | `/index.action` | 先点页面里的 `touch`、`whoami`、`pwd` 按钮，再观察 `/tmp` 标记和输出文件状态。 |
| Struts2 S2-003 | `struts2-s2-003` | `9964` | `/index.action` | 点击页面内置 payload，观察 `session.user` 与 `session.isAdmin` 是否被恶意参数名污染。 |
| Struts2 S2-001 | `struts2-s2-001` | `9965` | `/login.action` | 用空密码触发回填，再观察用户名字段是否发生 OGNL 解析。 |
| Collections | `collections` | `9945` | `/playground` | 先触发 `touch /tmp/collections-success`，再访问 `/status` 查看执行状态。 |
| 业务逻辑漏洞集 | `logic_vul` | 未接入 compose | `/` | 单独运行后访问首页，验证伪造身份、越权和业务数据接口。 |

## 建议验证顺序

1. 先启动单体靶场，确认 `http://宿主机IP:5000/` 可以打开。
2. 在首页按项目名搜索，比如 `shiro`、`fastjson`、`druid`，逐个点“测试”。
3. `collections` 建议先走 `/playground -> touch 标记 -> 查看状态` 这一条链，确认反序列化链路已通。
4. 需要自定义 payload 时用“重放数据包”修改请求。
