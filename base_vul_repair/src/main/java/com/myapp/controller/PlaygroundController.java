package com.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaygroundController {

    @GetMapping(value = {"/", "/playground"}, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String index() {
        String xmlPayload = "<?xml version=\"1.0\"?><!DOCTYPE foo [<!ELEMENT foo ANY >< !ENTITY xxe SYSTEM \"file:///etc/passwd\" >]><foo>&xxe;</foo>".replace("< !", "<!");

        return "<!DOCTYPE html>" +
                "<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>base_vul_repair Playground</title>" +
                style() +
                "</head><body><div class=\"container\"><h1>base_vul_repair Playground</h1>" +
                "<p class=\"hint\">These presets target the repaired endpoints. Fill a scenario, adjust it if needed, then send the request to observe the repaired behavior.</p>" +
                "<label for=\"scenario\">Scenario</label><select id=\"scenario\"></select>" +
                "<label for=\"method\">Method</label><input id=\"method\" value=\"GET\" />" +
                "<label for=\"path\">Path</label><input id=\"path\" value=\"/users1/1'/\" />" +
                "<label for=\"contentType\">Content-Type</label><input id=\"contentType\" value=\"application/json\" />" +
                "<label for=\"body\">Request Body</label><textarea id=\"body\"></textarea>" +
                "<label for=\"file\">Upload File</label><input id=\"file\" type=\"file\" />" +
                "<div class=\"actions\"><button class=\"fill\" onclick=\"fillSelected()\">Fill Selected Scenario</button><button class=\"send\" onclick=\"sendCurrent()\">Send Current Request</button></div>" +
                "<label for=\"result\">Response</label><pre id=\"result\">Waiting for request...</pre></div>" +
                "<script>const scenarios=[" +
                scenario("SQL repair users1 interceptor", "GET", "/users1/1'/", "application/json", "", false) + "," +
                scenario("SQL repair users2 long conversion", "GET", "/users2/1'/", "application/json", "", false) + "," +
                scenario("SQL repair Optional", "GET", "/users/findByOptionalUsername?username=test'", "application/json", "", false) + "," +
                scenario("XSS repair htmlEscape", "GET", "/xss_reflect_htmlEscape?name=<script>alert(123)</script>", "application/json", "", false) + "," +
                scenario("XSS repair escapeHtml4", "GET", "/xss_reflect_escapeHtml4?name=<script>alert(123)</script>", "application/json", "", false) + "," +
                scenario("XSS repair thymeleaf storage", "GET", "/xss_storage_thymeleaf?name=<script>alert(123)</script>", "application/json", "", false) + "," +
                scenario("File upload repair", "POST", "/file_upload", "multipart/form-data", "", true) + "," +
                scenario("File read repair", "GET", "/file_read?filePath=pom.xml", "application/json", "", false) + "," +
                scenario("File read repair safe method", "GET", "/file_read1?filePath=pom.xml", "application/json", "", false) + "," +
                scenario("File write repair", "GET", "/file_write?fileName=test.txt&data=test", "application/json", "", false) + "," +
                scenario("Runtime repair blocked", "GET", "/runtime_command_execute?command=whoami", "application/json", "", false) + "," +
                scenario("Runtime normal allowed", "GET", "/runtime_command_execute?command=date", "application/json", "", false) + "," +
                scenario("SpEL repair blocked", "GET", "/spel_expression?input=T(java.lang.Runtime).getRuntime().exec('whoami')", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SpEL normal", "GET", "/spel_expression?input=1", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF repair blocked", "GET", "/ssrf_openStream?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF normal", "GET", "/ssrf_openStream?url=http://example.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSTI repair", "GET", "/ssti_velocity?content=%23set (%24exp %3d \"exp\")%3b%24exp.getClass().forName(\"java.lang.Runtime\").getRuntime().exec(\"whoami\")", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("XXE repair", "POST", "/xxe_documentbuilderfactory", "application/xml", xmlPayload, false) + "," +
                scenario("Open redirect repair", "GET", "/OpenRedirector_ModelAndView?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("Open redirect normal", "GET", "/OpenRedirector_ModelAndView?url=https://example.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("swagger-ui repair", "GET", "/swagger-ui.html", "application/x-www-form-urlencoded", "", false) +
                "];" +
                commonScript() +
                "</script></body></html>";
    }

    private String scenario(String name, String method, String path, String contentType, String body, boolean useFile) {
        return "{name:'" + esc(name) + "',method:'" + esc(method) + "',path:'" + esc(path) + "',contentType:'" + esc(contentType) + "',body:'" + esc(body) + "',useFile:" + useFile + "}";
    }

    private String commonScript() {
        return "const scenarioBox=document.getElementById('scenario');const methodBox=document.getElementById('method');const pathBox=document.getElementById('path');const contentTypeBox=document.getElementById('contentType');const bodyBox=document.getElementById('body');const fileBox=document.getElementById('file');const resultBox=document.getElementById('result');" +
                "scenarios.forEach((item,index)=>{const option=document.createElement('option');option.value=index;option.textContent=item.name;scenarioBox.appendChild(option);});" +
                "function fillScenario(index){const item=scenarios[index];methodBox.value=item.method;pathBox.value=item.path;contentTypeBox.value=item.contentType;bodyBox.value=item.body;}" +
                "function fillSelected(){fillScenario(scenarioBox.value||0);}" +
                "async function sendCurrent(){resultBox.textContent='Request in progress...';try{const method=methodBox.value.trim().toUpperCase();const path=pathBox.value.trim();const contentType=contentTypeBox.value.trim();let response;if(contentType==='multipart/form-data'){const formData=new FormData();if(fileBox.files.length>0){formData.append('file',fileBox.files[0]);}response=await fetch(path,{method:method,body:formData});}else{const options={method:method,headers:{}};if(method!=='GET'&&bodyBox.value!==''){options.body=bodyBox.value;if(contentType!==''){options.headers['Content-Type']=contentType;}}response=await fetch(path,options);}const text=await response.text();resultBox.textContent='HTTP '+response.status+'\\n'+text;}catch(error){resultBox.textContent='Request failed: '+error;}}" +
                "fillScenario(0);";
    }

    private String style() {
        return "<style>body{font-family:Arial,sans-serif;margin:40px;background:#f5f7fb;color:#1f2937;}.container{max-width:980px;margin:0 auto;background:#fff;padding:24px;border-radius:12px;box-shadow:0 10px 30px rgba(0,0,0,0.08);}input,textarea,select,pre{width:100%;box-sizing:border-box;}input,textarea,select{padding:12px;border-radius:8px;border:1px solid #d0d7de;margin-bottom:16px;}textarea{min-height:180px;resize:vertical;}pre{min-height:180px;padding:12px;border-radius:8px;background:#111827;color:#e5e7eb;overflow:auto;}.actions{display:flex;gap:12px;margin:20px 0;}button{padding:12px 18px;border:0;border-radius:8px;cursor:pointer;font-size:14px;color:#fff;}.fill{background:#1f6feb;}.send{background:#111827;}.hint{color:#6b7280;font-size:14px;}</style>";
    }

    private String esc(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'").replace("\r", "").replace("\n", "\\n");
    }
}
