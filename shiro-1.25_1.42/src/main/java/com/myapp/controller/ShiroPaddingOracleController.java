package com.myapp.controller;

import com.myapp.support.RememberMeOracleService;
import com.myapp.support.RememberMeOracleService.OracleResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ShiroPaddingOracleController {

    private final RememberMeOracleService oracleService;

    public ShiroPaddingOracleController(RememberMeOracleService oracleService) {
        this.oracleService = oracleService;
    }

    @GetMapping(value = {"/", "/login-page", "/shiro-1.25_1.42"}, produces = "text/html;charset=UTF-8")
    public String index() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Shiro 1.2.5-1.4.1 Padding Oracle 靶场</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; background: #f4f7fb; margin: 0; padding: 32px 16px; color: #1f2937; }\n" +
                "        .wrap { max-width: 1160px; margin: 0 auto; }\n" +
                "        .card { background: #ffffff; border: 1px solid #d5dfeb; border-radius: 14px; padding: 22px; margin-bottom: 20px; box-shadow: 0 12px 28px rgba(15, 23, 42, 0.06); }\n" +
                "        h1, h2 { margin-top: 0; }\n" +
                "        p { line-height: 1.65; }\n" +
                "        input, textarea { width: 100%; box-sizing: border-box; padding: 10px; border: 1px solid #c8d4e3; border-radius: 10px; font-size: 14px; }\n" +
                "        textarea { min-height: 170px; resize: vertical; font-family: Consolas, monospace; }\n" +
                "        button { background: #0f172a; color: #ffffff; border: 0; border-radius: 10px; padding: 10px 16px; cursor: pointer; font-size: 14px; }\n" +
                "        button:hover { background: #1e293b; }\n" +
                "        .row { display: flex; gap: 10px; flex-wrap: wrap; }\n" +
                "        .grid { display: grid; gap: 16px; grid-template-columns: repeat(auto-fit, minmax(340px, 1fr)); }\n" +
                "        .mono { font-family: Consolas, monospace; word-break: break-all; }\n" +
                "        .pill { display: inline-block; padding: 4px 10px; border-radius: 999px; background: #eef2ff; color: #312e81; margin-right: 8px; margin-bottom: 8px; }\n" +
                "        pre { white-space: pre-wrap; word-break: break-word; background: #0f172a; color: #e2e8f0; border-radius: 12px; padding: 14px; overflow: auto; }\n" +
                "        table { width: 100%; border-collapse: collapse; font-size: 13px; }\n" +
                "        th, td { border: 1px solid #d2dbea; padding: 8px; text-align: left; vertical-align: top; }\n" +
                "        th { background: #eff6ff; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"wrap\">\n" +
                "        <div class=\"card\">\n" +
                "            <h1>Apache Shiro 1.2.5 - 1.4.1 Padding Oracle 靶场</h1>\n" +
                "            <p>该靶场聚焦 <strong>CVE-2019-12422</strong>。通过比较畸形 rememberMe Cookie 在 AES-CBC 下触发的不同状态码，演示 Oracle 差异行为。</p>\n" +
                "            <div>\n" +
                "                <span class=\"pill\">受影响版本: 1.2.5, 1.2.6, 1.3.0, 1.3.1, 1.3.2, 1.4.0-RC2, 1.4.0, 1.4.1</span>\n" +
                "                <span class=\"pill\">修复版本: 1.4.2</span>\n" +
                "            </div>\n" +
                "            <p>本地演示 key: <span class=\"mono\">" + RememberMeOracleService.DEMO_KEY_BASE64 + "</span></p>\n" +
                "        </div>\n" +
                "        <div class=\"grid\">\n" +
                "            <div class=\"card\">\n" +
                "                <h2>样本与探测</h2>\n" +
                "                <div class=\"row\">\n" +
                "                    <button type=\"button\" onclick=\"loadSample('valid')\">加载正常样本</button>\n" +
                "                    <button type=\"button\" onclick=\"loadSample('padding')\">加载 Padding 错误样本</button>\n" +
                "                    <button type=\"button\" onclick=\"loadSample('content')\">加载内容错误样本</button>\n" +
                "                </div>\n" +
                "                <p><strong>rememberMe Cookie</strong></p>\n" +
                "                <textarea id=\"cookieBox\" placeholder=\"用于探测的 Cookie 值\"></textarea>\n" +
                "                <div class=\"row\">\n" +
                "                    <button type=\"button\" onclick=\"probeCookie()\">开始探测</button>\n" +
                "                    <button type=\"button\" onclick=\"loadInfo()\">查看靶场信息</button>\n" +
                "                </div>\n" +
                "                <pre id=\"oracleResult\">等待操作...</pre>\n" +
                "            </div>\n" +
                "            <div class=\"card\">\n" +
                "                <h2>单字节翻转</h2>\n" +
                "                <p>对单个字节执行 XOR 翻转，并立即测试变异后的 Cookie。</p>\n" +
                "                <label>字节下标</label>\n" +
                "                <input id=\"mutIndex\" type=\"number\" value=\"16\" min=\"0\" />\n" +
                "                <label>XOR 值（十进制 0-255）</label>\n" +
                "                <input id=\"mutXor\" type=\"number\" value=\"1\" min=\"0\" max=\"255\" />\n" +
                "                <div class=\"row\">\n" +
                "                    <button type=\"button\" onclick=\"mutateAndProbe()\">翻转并探测</button>\n" +
                "                </div>\n" +
                "                <pre id=\"mutateResult\">等待翻转...</pre>\n" +
                "            </div>\n" +
                "            <div class=\"card\">\n" +
                "                <h2>字节遍历（非 Key 遍历）</h2>\n" +
                "                <p>遍历同一个 Cookie 的字节位并记录状态差异，不做 Key 爆破。</p>\n" +
                "                <label>起始下标</label>\n" +
                "                <input id=\"sweepStart\" type=\"number\" value=\"0\" min=\"0\" />\n" +
                "                <label>数量（最大 64）</label>\n" +
                "                <input id=\"sweepCount\" type=\"number\" value=\"32\" min=\"1\" max=\"64\" />\n" +
                "                <label>XOR 值（十进制 0-255）</label>\n" +
                "                <input id=\"sweepXor\" type=\"number\" value=\"1\" min=\"0\" max=\"255\" />\n" +
                "                <div class=\"row\">\n" +
                "                    <button type=\"button\" onclick=\"runSweep()\">开始遍历</button>\n" +
                "                </div>\n" +
                "                <div id=\"sweepTable\"></div>\n" +
                "            </div>\n" +
                "            <div class=\"card\">\n" +
                "                <h2>RememberMe 登录验证</h2>\n" +
                "                <form action=\"/login\" method=\"post\">\n" +
                "                    <label>用户名</label>\n" +
                "                    <input name=\"username\" value=\"admin\" />\n" +
                "                    <label>密码</label>\n" +
                "                    <input type=\"password\" name=\"password\" value=\"admin123\" />\n" +
                "                    <label><input type=\"checkbox\" name=\"rememberMe\" value=\"true\" checked /> 记住我</label>\n" +
                "                    <div class=\"row\">\n" +
                "                        <button type=\"submit\">登录</button>\n" +
                "                        <button type=\"button\" onclick=\"window.location.href='/profile'\">/profile</button>\n" +
                "                        <button type=\"button\" onclick=\"window.location.href='/admin'\">/admin</button>\n" +
                "                        <button type=\"button\" onclick=\"window.location.href='/logout'\">/logout</button>\n" +
                "                    </div>\n" +
                "                </form>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        async function loadSample(type) {\n" +
                "            const res = await fetch('/oracle/sample?type=' + encodeURIComponent(type));\n" +
                "            const data = await res.json();\n" +
                "            document.getElementById('cookieBox').value = data.cookie;\n" +
                "            document.getElementById('oracleResult').textContent = data.name + '\\n' + data.description;\n" +
                "        }\n" +
                "\n" +
                "        async function probeCookie() {\n" +
                "            const value = document.getElementById('cookieBox').value;\n" +
                "            const body = 'cookieValue=' + encodeURIComponent(value);\n" +
                "            const res = await fetch('/oracle/probe', {\n" +
                "                method: 'POST',\n" +
                "                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },\n" +
                "                body: body\n" +
                "            });\n" +
                "            const data = await res.json();\n" +
                "            document.getElementById('oracleResult').textContent =\n" +
                "                'HTTP ' + res.status + '\\n' +\n" +
                "                '阶段: ' + data.stage + '\\n' +\n" +
                "                '说明: ' + data.message + '\\n' +\n" +
                "                '主体: ' + (data.principal || '-');\n" +
                "        }\n" +
                "\n" +
                "        async function mutateAndProbe() {\n" +
                "            const value = document.getElementById('cookieBox').value;\n" +
                "            const index = document.getElementById('mutIndex').value;\n" +
                "            const xor = document.getElementById('mutXor').value;\n" +
                "            const body = 'cookieValue=' + encodeURIComponent(value) +\n" +
                "                '&index=' + encodeURIComponent(index) +\n" +
                "                '&xor=' + encodeURIComponent(xor);\n" +
                "\n" +
                "            const mutRes = await fetch('/oracle/mutate', {\n" +
                "                method: 'POST',\n" +
                "                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },\n" +
                "                body: body\n" +
                "            });\n" +
                "            const mutData = await mutRes.json();\n" +
                "            if (!mutData.ok) {\n" +
                "                document.getElementById('mutateResult').textContent = mutData.message;\n" +
                "                return;\n" +
                "            }\n" +
                "\n" +
                "            document.getElementById('cookieBox').value = mutData.cookie;\n" +
                "            const probeBody = 'cookieValue=' + encodeURIComponent(mutData.cookie);\n" +
                "            const probeRes = await fetch('/oracle/probe', {\n" +
                "                method: 'POST',\n" +
                "                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },\n" +
                "                body: probeBody\n" +
                "            });\n" +
                "            const probeData = await probeRes.json();\n" +
                "            document.getElementById('mutateResult').textContent =\n" +
                "                'index=' + mutData.index + ', xor=' + mutData.xor + ', before=' + mutData.before + ', after=' + mutData.after + '\\n' +\n" +
                "                'HTTP ' + probeRes.status + ', 阶段=' + probeData.stage + '\\n' +\n" +
                "                probeData.message;\n" +
                "        }\n" +
                "\n" +
                "        async function runSweep() {\n" +
                "            const value = document.getElementById('cookieBox').value;\n" +
                "            const start = document.getElementById('sweepStart').value;\n" +
                "            const count = document.getElementById('sweepCount').value;\n" +
                "            const xor = document.getElementById('sweepXor').value;\n" +
                "            const body = 'cookieValue=' + encodeURIComponent(value) +\n" +
                "                '&start=' + encodeURIComponent(start) +\n" +
                "                '&count=' + encodeURIComponent(count) +\n" +
                "                '&xor=' + encodeURIComponent(xor);\n" +
                "\n" +
                "            const res = await fetch('/oracle/sweep', {\n" +
                "                method: 'POST',\n" +
                "                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },\n" +
                "                body: body\n" +
                "            });\n" +
                "            const data = await res.json();\n" +
                "            if (!data.ok) {\n" +
                "                document.getElementById('sweepTable').textContent = data.message;\n" +
                "                return;\n" +
                "            }\n" +
                "\n" +
                "            const rows = data.records.map(r =>\n" +
                "                '<tr><td>' + r.index + '</td><td>' + r.status + '</td><td>' + r.stage + '</td><td>' + r.message + '</td></tr>'\n" +
                "            ).join('');\n" +
                "            document.getElementById('sweepTable').innerHTML =\n" +
                "                '<p>已遍历 ' + data.count + ' 个字节位（xor=' + data.xor + '）</p>' +\n" +
                "                '<table><thead><tr><th>index</th><th>status</th><th>stage</th><th>message</th></tr></thead><tbody>' + rows + '</tbody></table>';\n" +
                "        }\n" +
                "\n" +
                "        async function loadInfo() {\n" +
                "            const res = await fetch('/oracle/info');\n" +
                "            const data = await res.json();\n" +
                "            document.getElementById('oracleResult').textContent =\n" +
                "                'Shiro 版本: ' + data.shiroVersion + '\\n' +\n" +
                "                '演示 key: ' + data.demoKey + '\\n' +\n" +
                "                '受影响版本: ' + data.affectedVersions.join(', ') + '\\n' +\n" +
                "                '修复版本: ' + data.fixedVersion;\n" +
                "        }\n" +
                "\n" +
                "        loadSample('valid');\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    @PostMapping(value = "/login", produces = "text/plain;charset=UTF-8")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(defaultValue = "false") boolean rememberMe) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
        try {
            subject.login(token);
            return "登录成功，rememberMe=" + rememberMe + "。访问 /profile 查看状态。";
        } catch (AuthenticationException ex) {
            return "登录失败：" + ex.getMessage();
        }
    }

    @GetMapping(value = "/oracle/sample", produces = "application/json;charset=UTF-8")
    public Map<String, Object> oracleSample(@RequestParam(defaultValue = "valid") String type) {
        return oracleService.sample(type);
    }

    @PostMapping(value = "/oracle/probe", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> oracleProbe(@RequestParam("cookieValue") String cookieValue) {
        OracleResult result = oracleService.probe(cookieValue);
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("stage", result.getStage());
        body.put("message", result.getMessage());
        body.put("principal", result.getPrincipal());
        return new ResponseEntity<Map<String, Object>>(body, HttpStatus.valueOf(result.getStatus()));
    }

    @PostMapping(value = "/oracle/mutate", produces = "application/json;charset=UTF-8")
    public Map<String, Object> oracleMutate(@RequestParam("cookieValue") String cookieValue,
                                            @RequestParam("index") int index,
                                            @RequestParam(value = "xor", defaultValue = "1") int xor) {
        return oracleService.mutate(cookieValue, index, xor);
    }

    @PostMapping(value = "/oracle/sweep", produces = "application/json;charset=UTF-8")
    public Map<String, Object> oracleSweep(@RequestParam("cookieValue") String cookieValue,
                                           @RequestParam(value = "start", defaultValue = "0") int start,
                                           @RequestParam(value = "count", defaultValue = "32") int count,
                                           @RequestParam(value = "xor", defaultValue = "1") int xor) {
        return oracleService.sweep(cookieValue, start, count, xor);
    }

    @GetMapping(value = "/oracle/info", produces = "application/json;charset=UTF-8")
    public Map<String, Object> oracleInfo() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("shiroVersion", "1.4.1");
        result.put("demoKey", oracleService.getDemoKeyBase64());
        result.put("affectedVersions", Arrays.asList("1.2.5", "1.2.6", "1.3.0", "1.3.1", "1.3.2", "1.4.0-RC2", "1.4.0", "1.4.1"));
        result.put("fixedVersion", "1.4.2");
        return result;
    }

    @GetMapping(value = "/profile", produces = "text/plain;charset=UTF-8")
    public String profile() {
        Subject subject = SecurityUtils.getSubject();
        String principal = subject.getPrincipal() == null ? "anonymous" : subject.getPrincipal().toString();
        return "principal=" + principal +
                ", authenticated=" + subject.isAuthenticated() +
                ", remembered=" + subject.isRemembered();
    }

    @GetMapping(value = "/admin", produces = "text/plain;charset=UTF-8")
    public String admin() {
        Subject subject = SecurityUtils.getSubject();
        return "admin resource reached by " + subject.getPrincipal();
    }

    @GetMapping(value = "/health", produces = "text/plain;charset=UTF-8")
    public String health() {
        return "ok";
    }
}
