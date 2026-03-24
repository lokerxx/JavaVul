<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-009 靶场</title>
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
        <h1>Struts2 S2-009 演示靶场</h1>
        <p>这个模块用来演示 <code>S2-009 / CVE-2011-3923</code>。核心是先把表达式放进正常参数 <code>name</code>，再用 <code>z[(name)('meh')]=true</code> 触发它作为 OGNL 二次求值。</p>
    </div>
    <div class="card">
        <h2>推荐入口</h2>
        <p><a href="example5.action">打开示例页 /example5.action</a></p>
        <pre>GET /example5.action?age=123&name=(#context["xwork.MethodAccessor.denyMethodExecution"]=new java.lang.Boolean(false),#_memberAccess["allowStaticMethodAccess"]=new java.lang.Boolean(true),...)(meh)&z[(name)('meh')]=true</pre>
        <p>推荐先访问示例页，再点击页面里的重放按钮。</p>
    </div>
</div>
</body>
</html>
