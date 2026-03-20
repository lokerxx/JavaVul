# shior-1.2.4 操作教程

- 类型：单体靶场
- 目录：`shior-1.2.4`
- 端口：`9970`
- 推荐入口：`/shiro-1.2.4`

## 这是什么

Shiro 1.2.4 RememberMe 靶场

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 等待对应容器启动完成，并确认端口 `9970` 已经监听。
3. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `shiro_1_2_4、shiro-1.2.4、rememberme` 过滤到当前项目。
3. 先点“测试”发送内置模板。
4. 推荐先跑 `shiro_1_2_4_attack`，再按需用“重放数据包”替换 payload。
5. 再跑 `shiro_1_2_4_normal` 做正常流量或修复版对照。

推荐直接使用的首页条目：
- `shiro_1_2_4_attack`：GET http://宿主机IP:9970/rememberme/check
- `shiro_1_2_4_normal`：POST http://宿主机IP:9970/login

## 方式二：直接访问接口测试

1. 先访问推荐入口：`http://宿主机IP:9970/shiro-1.2.4`。
2. 先执行攻击面请求：`shiro_1_2_4_attack`。
3. 可直接复制命令：`curl "http://宿主机IP:9970/rememberme/check" -H "Content-Type: application/json"`。
4. 再执行对照请求：`shiro_1_2_4_normal`。
5. 对照命令：`curl -X POST "http://宿主机IP:9970/login" -H "Content-Type: application/x-www-form-urlencoded" -d "username=admin&password=admin123&rememberMe=true"`。

## 测试时重点看什么

1. 先看首页按钮或接口是否能正常返回 RememberMe / Oracle 的检测结果。
2. 再看登录后访问受保护资源时，响应状态和页面内容有没有变化。
3. 如果你修改了 Cookie 或表单参数，重点对比前后响应差异。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
