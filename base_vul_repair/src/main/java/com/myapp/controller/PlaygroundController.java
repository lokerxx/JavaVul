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
                "<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>base_vul_repair Playground</title>" +
                style() +
                "</head><body><div class=\"container\"><h1>base_vul_repair Playground</h1>" +
                "<p class=\"hint\">这里展示的是修复版接口。请选择一个预设场景，切换后会自动回填方法、路径、内容类型和请求体，方便对比修复后的行为。</p>" +
                "<label for=\"scenario\">场景</label><select id=\"scenario\" size=\"18\" onchange=\"fillSelected()\"></select>" +
                "<label for=\"method\">请求方法</label><input id=\"method\" value=\"GET\" />" +
                "<label for=\"path\">Path</label><input id=\"path\" value=\"/users1/1'/\" />" +
                "<label for=\"contentType\">内容类型</label><input id=\"contentType\" value=\"application/json\" />" +
                "<label for=\"body\">请求体</label><textarea id=\"body\"></textarea>" +
                "<label for=\"file\">上传文件</label><input id=\"file\" type=\"file\" />" +
                "<div class=\"actions\"><button class=\"fill\" onclick=\"fillSelected()\">回填当前场景</button><button class=\"send\" onclick=\"sendCurrent()\">发送请求</button><button class=\"open\" onclick=\"openCurrent()\">浏览器中打开</button></div>" +
                "<p class=\"hint\">说明：发送请求会以文本形式展示响应，适合观察修复后的返回值。若要观察页面渲染或重定向效果，请使用“浏览器中打开”。场景已按类别分组显示。</p>" +
                "<label for=\"result\">响应结果</label><pre id=\"result\">等待发送请求...</pre></div>" +
                "<script>const scenarios=[" +
                scenario("SQL 注入修复", "users 原始路径参数", "GET", "/users/1/", "application/json", "", false) + "," +
                scenario("SQL 注入修复", "users1 拦截器拦截注入", "GET", "/users1/1'/", "application/json", "", false) + "," +
                scenario("SQL 注入修复", "users1 正常请求", "GET", "/users1/1/", "application/json", "", false) + "," +
                scenario("SQL 注入修复", "users2 Long 转换拦截注入", "GET", "/users2/1'/", "application/json", "", false) + "," +
                scenario("SQL 注入修复", "users2 正常请求", "GET", "/users2/1/", "application/json", "", false) + "," +
                scenario("SQL 注入修复", "Optional 参数修复", "GET", "/users/findByOptionalUsername?username=test'", "application/json", "", false) + "," +
                scenario("SQL 注入修复", "对象参数修复", "POST", "/users/get_name_object", "application/json", "{\"name\":\"test'\"}", false) + "," +
                scenario("SQL 注入修复", "MyBatis 注解查询", "GET", "/users/by-username?name=test", "application/json", "", false) + "," +
                scenario("SQL 注入修复", "Lombok 查询", "POST", "/users/lombok", "application/json", "{\"name\":\"test\"}", false) + "," +
                scenario("SQL 注入修复", "ids 查询", "GET", "/users/ids?ids=1,2,3", "application/json", "", false) + "," +
                scenario("SQL 注入修复", "name 模糊查询", "GET", "/users/name?name=A", "application/json", "", false) + "," +
                scenario("SQL 注入修复", "sort 查询", "GET", "/users/sort?orderByColumn=name&orderByDirection=asc", "application/json", "", false) + "," +
                scenario("SQL 注入修复", "names 列表查询", "GET", "/users/names?names=Alice&names=Bob", "application/json", "", false) + "," +
                scenario("XSS 修复", "reflect escapeHtml", "GET", "/xss_reflect_escapeHtml?name=<script>alert(123)</script>", "application/json", "", false) + "," +
                scenario("XSS 修复", "reflect htmlEscape", "GET", "/xss_reflect_htmlEscape?name=<script>alert(123)</script>", "application/json", "", false) + "," +
                scenario("XSS 修复", "reflect escapeHtml4", "GET", "/xss_reflect_escapeHtml4?name=<script>alert(123)</script>", "application/json", "", false) + "," +
                scenario("XSS 修复", "storage thymeleaf", "GET", "/xss_storage_thymeleaf?name=%3Cscript%3Ealert(123)%3C/script%3E", "application/json", "", false) + "," +
                scenario("文件修复", "文件上传页面", "GET", "/file_upload", "application/json", "", false) + "," +
                scenario("文件修复", "文件上传", "POST", "/file_upload", "multipart/form-data", "", true) + "," +
                scenario("文件修复", "文件读取 canonical 校验", "GET", "/file_read?filePath=uploads/test.log", "application/json", "", false) + "," +
                scenario("文件修复", "文件读取 safe resolve", "GET", "/file_read1?filePath=test.log", "application/json", "", false) + "," +
                scenario("文件修复", "文件读取 filter 校验", "GET", "/file_read2?filePath=test.log", "application/json", "", false) + "," +
                scenario("文件修复", "文件读取字符串过滤", "GET", "/file_read3?filePath=uploads/test.log", "application/json", "", false) + "," +
                scenario("文件修复", "文件写入被拒绝示例", "GET", "/file_write?fileName=test.txt&data=test", "application/json", "", false) + "," +
                scenario("文件修复", "文件写入合法 .log", "GET", "/file_write?fileName=test.log&data=test", "application/json", "", false) + "," +
                scenario("文件修复", "文件下载被拒绝示例", "GET", "/file_download?fileName=../pom.xml", "application/json", "", false) + "," +
                scenario("文件修复", "文件下载合法 .log", "GET", "/file_download?fileName=test.log", "application/json", "", false) + "," +
                scenario("文件修复", "文件删除被拒绝示例", "GET", "/file_delete?fileName=../test", "application/json", "", false) + "," +
                scenario("文件修复", "文件删除合法名称", "GET", "/file_delete?fileName=test", "application/json", "", false) + "," +
                scenario("命令执行与表达式修复", "Runtime 拦截 whoami", "GET", "/runtime_command_execute?command=whoami", "application/json", "", false) + "," +
                scenario("命令执行与表达式修复", "Runtime 允许 date", "GET", "/runtime_command_execute?command=date", "application/json", "", false) + "," +
                scenario("命令执行与表达式修复", "ProcessBuilder 拦截 whoami", "GET", "/process_builder_command_execute?command=whoami", "application/json", "", false) + "," +
                scenario("命令执行与表达式修复", "ProcessBuilder 允许 echo", "GET", "/process_builder_command_execute?command=echo hello", "application/json", "", false) + "," +
                scenario("命令执行与表达式修复", "SpEL 拦截危险表达式", "GET", "/spel_expression?input=T(java.lang.Runtime).getRuntime().exec('whoami')", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("命令执行与表达式修复", "SpEL 正常表达式", "GET", "/spel_expression?input='hello'", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("命令执行与表达式修复", "SSTI Velocity 过滤危险字符", "GET", "/ssti_velocity?content=%23set (%24exp %3d \"exp\")%3b%24exp.getClass().forName(\"java.lang.Runtime\").getRuntime().exec(\"whoami\")", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("请求处理修复", "CRLF 注入测试", "GET", "/crlf_injection?name=%0D%0ASet-Cookie: sessionid=123456", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("请求处理修复", "开放重定向拦截外站", "GET", "/OpenRedirector_ModelAndView?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("请求处理修复", "开放重定向允许白名单", "GET", "/OpenRedirector_ModelAndView?url=https://example.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("请求处理修复", "sendRedirect 拦截外站", "GET", "/OpenRedirector_sendRedirect?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("请求处理修复", "Location 允许白名单", "GET", "/OpenRedirector_lacation?url=https://example.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF 修复", "openStream 拦截外站", "GET", "/ssrf_openStream?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF 修复", "openStream 允许白名单", "GET", "/ssrf_openStream?url=http://example.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF 修复", "openConnection 拦截外站", "GET", "/ssrf_openConnection?url=https://www.baidu.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF 修复", "Request.Get 允许白名单", "GET", "/ssrf_requestGet?url=http://example.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF 修复", "OkHttp 允许白名单", "GET", "/ssrf_okhttp?url=http://example.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("SSRF 修复", "DefaultHttpClient 允许白名单", "GET", "/ssrf_defaultHttpClient?url=http://example.com", "application/x-www-form-urlencoded", "", false) + "," +
                scenario("XXE 修复", "SAXParserFactory", "POST", "/xxe_saxparserfactory", "application/xml", xmlPayload, false) + "," +
                scenario("XXE 修复", "XMLReaderFactory", "POST", "/xxe_xmlreaderfactory", "application/xml", xmlPayload, false) + "," +
                scenario("XXE 修复", "SAXBuilder", "POST", "/xxe_saxbuilder", "application/xml", xmlPayload, false) + "," +
                scenario("XXE 修复", "SAXReader", "POST", "/xxe_saxreader", "application/xml", xmlPayload, false) + "," +
                scenario("XXE 修复", "DocumentHelper", "POST", "/xxe_documenthelper", "application/xml", xmlPayload, false) + "," +
                scenario("XXE 修复", "DocumentBuilderFactory", "POST", "/xxe_documentbuilderfactory", "application/xml", xmlPayload, false) + "," +
                scenario("XXE 修复", "DocumentBuilderFactory XInclude", "POST", "/xxe_documentbuilderfactory_xinclude", "application/xml", xmlXIncludePayload, false) + "," +
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
