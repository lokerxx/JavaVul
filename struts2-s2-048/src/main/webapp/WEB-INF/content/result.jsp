<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-048 结果</title>
    <style>
        body { margin: 0; font-family: "Helvetica Neue", "PingFang SC", sans-serif; background: #f5efe7; color: #1d2a35; }
        .wrap { max-width: 1040px; margin: 0 auto; padding: 28px 18px 40px; }
        .card { background: #fffdf8; border: 1px solid #d9ccb9; border-radius: 22px; padding: 24px; box-shadow: 0 16px 44px rgba(76, 51, 33, 0.08); margin-bottom: 18px; }
        .actions { display: flex; flex-wrap: wrap; gap: 10px; margin: 16px 0; }
        .preset-btn, .submit-btn { border: 0; border-radius: 999px; padding: 10px 16px; font-size: 14px; cursor: pointer; }
        .preset-btn { background: #e6f2ef; color: #155b52; }
        .submit-btn { background: #155b52; color: #fff; }
        .field { display: grid; gap: 8px; margin: 14px 0 18px; }
        .field label { font-weight: 600; }
        .field textarea, .field input { width: 100%; box-sizing: border-box; padding: 14px; border-radius: 14px; border: 1px solid #d9ccb9; background: #fff; color: #1d2a35; font: 14px/1.6 Consolas, monospace; }
        .field textarea { min-height: 160px; resize: vertical; }
        pre { background: #15202b; color: #e7eef7; padding: 14px; border-radius: 12px; white-space: pre-wrap; word-break: break-word; }
        p, li { line-height: 1.75; }

    </style>
</head>
<body>
<div class="wrap">
    <div class="card"><h1>表单提交结果</h1><p>Gangster Name：<code><s:property value="gangsterName"/></code></p><p><a href="index.action">返回测试页</a></p></div>
</div>

</body>
</html>
