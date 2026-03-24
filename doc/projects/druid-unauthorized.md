# druid_unauthorized 操作教程

- 类型：单体靶场
- 目录：`druid_unauthorized`
- 端口：`9997`
- 推荐入口：`/druid`

## 这是什么

Druid 未授权访问靶场

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 这个项目现在默认使用 SQLite，本地会自动初始化 `/tmp/druid_unauthorized.db`，不再依赖 MySQL。
3. 等待对应容器启动完成，并确认端口 `9997` 已经监听。
4. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `druid、unauthorized` 过滤到当前项目。
3. 先点“测试”发送内置模板。
4. 推荐先跑 `druid_unauthorized`，再按需用“重放数据包”替换 payload。
5. 再跑 `druid_sqlwall` 做正常流量或修复版对照。

推荐直接使用的首页条目：
- `druid_unauthorized`：GET http://宿主机IP:9997/druid
- `druid_sqlwall`：GET http://宿主机IP:9997/druid_sql?id=1

## 方式二：直接访问接口测试

1. 先访问推荐入口：`http://宿主机IP:9997/druid`。
2. 先执行攻击面请求：`druid_unauthorized`。
3. 可直接复制命令：`curl "http://宿主机IP:9997/druid" -H "Content-Type: application/json"`。
4. 再执行对照请求：`druid_sqlwall`。
5. 对照命令：`curl "http://宿主机IP:9997/druid_sql?id=1" -H "Content-Type: application/json"`。

## 测试时重点看什么

1. 漏洞版重点看敏感端点是否可以未授权访问。
2. 修复版重点看是否返回 401、403、登录页或更少的暴露信息。
3. 最好把漏洞版和修复版窗口并排打开做对照。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
