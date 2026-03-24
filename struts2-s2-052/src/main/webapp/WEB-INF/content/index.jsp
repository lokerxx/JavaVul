<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-052 靶场</title>
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
    <div class="card">
        <h1>Struts2 S2-052 演示靶场</h1>
        <p>这个模块接入了 <code>struts2-rest-plugin</code>，保留 <code>/orders/3/edit</code> 这类 REST 风格入口，便于用 <code>application/xml</code> 发送 XStream 负载。</p>
        <s:actionmessage/>
        <div class="actions">
            <a class="preset-btn" href="orders/3/edit">打开 /orders/3/edit</a>
        </div>
        <div class="field">
            <label>当前可直接复制的请求</label>
            <textarea readonly>POST /orders/3/edit
Content-Type: application/xml

&lt;map&gt;...ProcessBuilder gadget...&lt;/map&gt;</textarea>
        </div>
        <pre>curl -i -X POST "http://宿主机IP:9951/orders/3/edit" -H "Content-Type: application/xml" --data-binary @payload.xml</pre>
    </div>
</div>

</body>
</html>
