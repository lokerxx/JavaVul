# base_vul 操作教程

- 类型：单体靶场
- 目录：`base_vul`
- 端口：`9991`
- 推荐入口：`/swagger-ui.html`

## 这是什么

基础漏洞合集

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 这个项目现在默认使用 SQLite，本地会自动初始化 `/tmp/base_vul.db`，不再依赖 MySQL。
3. 等待对应容器启动完成，并确认端口 `9991` 已经监听。
4. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `base_vul、sql、xss、ssrf、xxe` 过滤到当前项目。
3. 先点“测试”发送内置模板。
4. 推荐先跑 `OpenRedirector_ModelAndView_attack`，再按需用“重放数据包”替换 payload。
5. 再跑 `ReDos_normal_1` 做正常流量或修复版对照。

推荐直接使用的首页条目：
- `OpenRedirector_ModelAndView_attack`：GET http://宿主机IP:9991/OpenRedirector_ModelAndView?url=https://宿主机IP
- `OpenRedirector_lacation_attack`：GET http://宿主机IP:9991/OpenRedirector_lacation?url=https://宿主机IP
- `OpenRedirector_sendRedirect_attack`：GET http://宿主机IP:9991/OpenRedirector_sendRedirect?url=https://宿主机IP

## 方式二：直接访问接口测试

1. 先访问推荐入口：`http://宿主机IP:9991/swagger-ui.html`。
2. 先执行攻击面请求：`OpenRedirector_ModelAndView_attack`。
3. 可直接复制命令：`curl "http://宿主机IP:9991/OpenRedirector_ModelAndView?url=https://宿主机IP" -H "Content-Type: application/x-www-form-urlencoded"`。
4. 再执行对照请求：`ReDos_normal_1`。
5. 对照命令：`curl "http://宿主机IP:9991/testReDos1?input=1" -H "Content-Type: application/x-www-form-urlencoded"`。

## 测试时重点看什么

1. 先发正常请求确认业务可用，再发攻击请求对比响应差异。
2. 对于 SQLi、XXE、SSRF、SSTI、文件读写等接口，建议关注回显和异常日志。
3. 如果项目同时有 repair 版，再用同一组 payload 做一次对照验证。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
