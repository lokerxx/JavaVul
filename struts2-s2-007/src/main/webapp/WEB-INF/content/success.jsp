<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>登录成功</title>
    <style>
        body { margin: 0; font-family: "Helvetica Neue", "PingFang SC", sans-serif; background: #f5efe7; color: #1d2a35; }
        .wrap { max-width: 780px; margin: 0 auto; padding: 28px 18px 40px; }
        .card { background: #fffdf8; border: 1px solid #d9ccb9; border-radius: 22px; padding: 24px; box-shadow: 0 16px 44px rgba(76, 51, 33, 0.08); }
        code { font-family: Consolas, monospace; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="card">
        <h1>提交成功</h1>
        <p>姓名：<code><s:property value="name"/></code></p>
        <p>邮箱：<code><s:property value="email"/></code></p>
        <p>年龄：<code><s:property value="age"/></code></p>
        <p><a href="user.action">返回用户资料页</a></p>
    </div>
</div>
</body>
</html>
