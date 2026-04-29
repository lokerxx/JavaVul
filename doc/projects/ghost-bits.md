# ghost-bits 操作教程

- 类型：单体靶场
- 目录：`ghost-bits`
- 端口：`9943`
- 推荐入口：`/ghost-bits`

## 这是什么

Ghost Bits / Cast Attack 综合演示靶场，核心是复现“安全检查看到的 Unicode 字符串”和“底层错误按低 8 位写出的字节”之间的语义差异。

## 漏洞描述

Java 是企业级应用里最常见的语言之一，Spring、Tomcat、Jackson、Fastjson 等框架与组件被大量业务系统长期依赖。Ghost Bits / Cast Attack 这一类问题的核心，不是某一个单独组件的普通逻辑漏洞，而是 Java 生态里长期存在的一类“字符视图”和“字节视图”不一致的系统性风险。

攻击者可以把原本明显带有攻击语义的 ASCII 载荷，替换成低 8 位一致、高 8 位不同的 Unicode 字符。这样一来，WAF、黑名单、人工审计和业务校验在检查阶段看到的是一串看似无意义的 Unicode；而到了后端某些错误的 `char -> byte` 转换、宽松解码或二次解析逻辑里，这些字符又会被还原成真实的危险字节，最终进入路径、协议、JSON、SQL、HTML、SMTP、Header 等安全敏感边界。

这类问题的危险性在于：检查时看到的是 A，执行时用到的是 B。只要应用在“安全校验之后”还会继续发生 low-byte 截断、宽松 URL/Hex 解码、二次 `%u` 解析或类似的宽容处理，就可能让原本被隐藏的攻击语义重新出现，形成 WAF 绕过、文件上传绕过、目录穿越、任意文件读取、CRLF 注入、Fastjson 关键字绕过、SQL 注入和 XSS 等高风险利用链。

## 缺陷成因

Ghost Bits 的根因可以概括成一句话：Java 的 `char` 是 16 位，而很多老式或不安全的处理路径只把它当作 8 位来写出。

典型危险写法包括：

- `(byte) ch`
- `ch & 0xff`
- `OutputStream.write(ch)`
- `ByteArrayOutputStream.write(ch)`
- `DataOutputStream.writeBytes(...)`

当这些代码把 `char` 强制转成 `byte` 时，高 8 位会被静默丢弃，只保留低 8 位。攻击者只要选取“低 8 位等于目标危险字符”的 Unicode，就能把：

- 看起来不是 `.jsp` 的文件名，落地成 `.jsp`
- 看起来不是 `../` 的路径，解码后变成目录穿越
- 看起来不是 `\r\n` 的文本，写出后变成协议换行
- 看起来不含 `@type`、`union select`、`<script>` 的输入，在解析后变成真实攻击载荷

## 影响面

公开研究和后续分析表明，这类问题不是单一 CVE，而是一类能影响大量 Java 框架、组件和业务代码的攻击模式。典型影响面包括：

- 文件上传与扩展名校验绕过
- 路径穿越与任意文件读取
- HTTP Header / SMTP / 文本协议中的 CRLF 注入
- Fastjson、Jackson 等解析器关键字段绕过
- SQL 注入、XSS 等业务 sink 前的关键字隐藏
- 反序列化、表达式执行、请求走私等更复杂利用链的前置绕过

研究人员曾按 `(byte) ch`、`ch & 0xff`、`writeBytes(...)`、`baos.write(ch)` 等模式在公开 Java 代码中进行检索，命中数量达到数千级别，说明这类缺陷并不罕见，而是很容易在自研代码、老旧库和兼容性逻辑中长期潜伏。

## 发现来源

这个靶场对应的背景来源，是 2026 年 4 月在 Black Hat Asia 2026 上公开的研究《Cast Attack: A New Threat Posed by Ghost Bits in Java》。

公开分享者包括：

- Zhihui Chen（1ue）
- Xinyu Bai（浅蓝）

这项研究系统性地梳理了 Java 生态中的 Ghost Bits 风险，并展示了它如何被用于绕过 WAF/IDS 等检测逻辑，进一步触发 SQL 注入、反序列化、文件上传、SMTP 注入、请求走私、XSS 等多类攻击链。本项目里的 `ghost-bits` 靶场，正是基于这类公开研究思路，整理出便于本地复现和教学验证的多场景演示环境。

页面内置了 4 组入口：

- `GET /api/inspect?input=...`：查看原始字符、低字节字符串和逐字符映射。
- `GET /api/upload-sink?filename=...`：模拟上传后缀检查与落盘文件名不一致，例如 `1.陪sp -> 1.jsp`。
- `GET /api/path-sink?path=...`：模拟路径检查早于最终解码，`阮严灵丰丰甲来/secret/flag.txt` 会折叠到 `../secret/flag.txt`。
- `GET /api/header-sink?value=...`：模拟协议边界上 CRLF 被低字节写出，`瘍瘊` 会变成 `\r\n`。
- `GET /api/json-autotype?payload=...`：模拟原始请求里没有 ASCII `@type`，但 low-byte 还原后被 Fastjson 解析成真实类型。
- `GET /api/json-user?payload=...`：模拟检查阶段看不到危险用户名，JSON 解析后却拿到真实业务字段。
- `GET /api/file-read-sink?path=...`：模拟多段幽灵路径先折叠成 `.%u002e/`，再经 `%u002e` 与 `%xx` 解码还原到目标文件，例如 `/etc/passwd`。
- `GET /api/sqli-sink?input=...`：模拟 low-byte 还原后出现 `union select` 或引号，再被拼接进 SQL 查询。
- `GET /api/xss-sink?input=...`：模拟 low-byte 还原后出现 `<script>` 等危险标签，再进入不转义的 HTML sink。

## 具体操作步骤

1. 运行统一编排，或单独在项目目录执行 `docker compose up --build`。
2. 打开 `http://宿主机IP:9943/ghost-bits`，先在“低字节视图”里输入 `陪`、`㹣౬ᙡ⑳⑳`、`瘍瘊` 看映射结果。
3. 在“上传扩展名绕过”里保持默认值 `1.陪sp`，观察 `validator_allows=true` 但 `stored_filename=1.jsp`。
4. 在“路径变形 / 双重解析”里保持默认值 `阮严灵丰丰甲来/secret/flag.txt`，观察它最终解析到 `../secret/flag.txt` 并越出 `public` 目录。
5. 在“CRLF / Header 注入”里保持默认值 `token瘍瘊X-Evil: yes`，观察 `parsed_lines` 被拆成两行。
6. 在“JSON / Fastjson @type”里直接发送默认 payload，观察原文不包含 ASCII `@type`，而 low-byte 视图已经还原成 `{"@type":"java.awt.Rectangle"...}`。
7. 在“JSON 字段绕过到业务语义”里发送默认 payload，观察解析后 `parsed_username=root@localhost`。
8. 在“多段幽灵路径到 /etc/passwd”里发送默认 payload，观察多次解码后 `resolved_path=/etc/passwd`，以及文件内容预览。
9. 在“Ghost Bits -> SQLi”里发送默认 payload，观察原文不包含 ASCII `union select`，但 `constructed_sql` 已经被 low-byte 注入。
10. 在“Ghost Bits -> XSS”里发送默认 payload，观察 `rendered_html` 中已经出现真实 `<script>` 标签。
11. 如需查看靶场目录状态，访问 `http://宿主机IP:9943/api/status`。

## 相关入口

- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
