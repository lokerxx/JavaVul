package com.myapp.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ShiroPaddingOracleController {

    @GetMapping(value = {"/", "/index.jsp", "/shiro-1.25_1.42"}, produces = "text/html;charset=UTF-8")
    public String index() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Shiro 1.4.1 Padding Oracle 靶场</title>\n" +
                "    <style>\n" +
                "        body { margin: 0; font-family: \"Helvetica Neue\", \"PingFang SC\", sans-serif; background: #f4efe7; color: #1d2a35; }\n" +
                "        .page { max-width: 1160px; margin: 0 auto; padding: 28px 18px 40px; }\n" +
                "        .hero, .panel { background: rgba(255, 253, 249, 0.95); border: 1px solid #dccdbd; border-radius: 22px; box-shadow: 0 16px 44px rgba(76, 51, 33, 0.08); }\n" +
                "        .hero { padding: 26px; margin-bottom: 20px; }\n" +
                "        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 18px; }\n" +
                "        .panel { padding: 22px; }\n" +
                "        h1, h2 { margin-top: 0; }\n" +
                "        p, li { line-height: 1.75; }\n" +
                "        .pill { display: inline-block; margin: 0 8px 8px 0; padding: 5px 12px; border-radius: 999px; background: #efe2d4; color: #7b3f1f; font-size: 13px; }\n" +
                "        .field { margin-bottom: 14px; }\n" +
                "        label { display: block; margin-bottom: 6px; font-weight: 600; }\n" +
                "        input[type=text], input[type=password] { width: 100%; padding: 11px 12px; border: 1px solid #cebba8; border-radius: 12px; box-sizing: border-box; }\n" +
                "        button { border: 0; border-radius: 12px; background: #8c4b2f; color: #fff; padding: 11px 18px; cursor: pointer; }\n" +
                "        code, pre { font-family: \"SFMono-Regular\", Consolas, monospace; }\n" +
                "        pre { background: #1d2630; color: #e9eef5; padding: 14px; border-radius: 14px; overflow: auto; white-space: pre-wrap; word-break: break-all; }\n" +
                "        a { color: #145b52; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"page\">\n" +
                "    <div class=\"hero\">\n" +
                "        <h1>Apache Shiro 1.4.1 Padding Oracle 靶场</h1>\n" +
                "        <p>当前实现仍然是 <strong>Spring Boot</strong>，但过滤链和登录流程已经尽量贴近官方 <code>samples/web</code>：通过 <code>IniShiroFilter + shiro.ini</code> 处理登录、RememberMe 和跳转，测试时先访问 <code>/login.jsp</code> 获取合法 <code>rememberMe</code>，再带 Cookie 请求 <code>/home.jsp</code> 观察差异响应。</p>\n" +
                "        <div>\n" +
                "            <span class=\"pill\">CVE-2019-12422</span>\n" +
                "            <span class=\"pill\">Apache Shiro 1.4.1</span>\n" +
                "            <span class=\"pill\">Spring Boot JAR</span>\n" +
                "            <span class=\"pill\">samples/web-like</span>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <div class=\"grid\">\n" +
                "        <div class=\"panel\">\n" +
                "            <h2>登录取 Cookie</h2>\n" +
                "            <p>测试账号建议先用：<code>root / secret</code>。</p>\n" +
                "            <form action=\"/login.jsp\" method=\"post\">\n" +
                "                <div class=\"field\"><label>用户名</label><input name=\"username\" type=\"text\" value=\"root\"></div>\n" +
                "                <div class=\"field\"><label>密码</label><input name=\"password\" type=\"password\" value=\"secret\"></div>\n" +
                "                <div class=\"field\"><label><input type=\"checkbox\" name=\"rememberMe\" value=\"true\" checked> Remember Me</label></div>\n" +
                "                <button type=\"submit\">登录并生成 rememberMe</button>\n" +
                "            </form>\n" +
                "        </div>\n" +
                "        <div class=\"panel\">\n" +
                "            <h2>怎么测</h2>\n" +
                "            <ol>\n" +
                "                <li>先访问 <code>/login.jsp</code> 并勾选 Remember Me，记录响应头里的 <code>rememberMe</code>。</li>\n" +
                "                <li>携带合法 Cookie 访问 <code>/home.jsp</code>，应能进入页面，通常不会回写 <code>rememberMe=deleteMe</code>。</li>\n" +
                "                <li>把 Cookie 改成畸形值后再请求 <code>/home.jsp</code>，观察响应头是否出现 <code>Set-Cookie: rememberMe=deleteMe</code>。</li>\n" +
                "                <li>真实利用时，Padding Oracle 的关键就是区分这两类响应差异。</li>\n" +
                "            </ol>\n" +
                "        </div>\n" +
                "        <div class=\"panel\">\n" +
                "            <h2>Curl 示例</h2>\n" +
                "            <pre>curl -i -X POST \"http://宿主机IP:9969/login.jsp\" \\\n" +
                "-H \"Content-Type: application/x-www-form-urlencoded\" \\\n" +
                "-d \"username=root&password=secret&rememberMe=true\"</pre>\n" +
                "            <pre>curl -i \"http://宿主机IP:9969/home.jsp\" \\\n" +
                "-H \"Cookie: rememberMe=把上一步拿到的合法Cookie填这里\"</pre>\n" +
                "            <pre>curl -i \"http://宿主机IP:9969/home.jsp\" \\\n" +
                "-H \"Cookie: rememberMe=QUFB\"</pre>\n" +
                "        </div>\n" +
                "        <div class=\"panel\">\n" +
                "            <h2>辅助入口</h2>\n" +
                "            <p><a href=\"/login.jsp\">/login.jsp</a></p>\n" +
                "            <p><a href=\"/home.jsp\">/home.jsp</a></p>\n" +
                "            <p><a href=\"/account/index.jsp\">/account/index.jsp</a></p>\n" +
                "            <p><a href=\"/oracle/info\">/oracle/info</a></p>\n" +
                "            <p><a href=\"/logout\">/logout</a></p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }

    @GetMapping(value = "/login.jsp", produces = "text/html;charset=UTF-8")
    public String loginPage() {
        String failure = String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("shiroLoginFailure"));
        String errorBlock = "null".equals(failure) ? "" : "<p style=\"color:#a32626;\">登录失败：" + failure + "</p>";
        return "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>Login</title>" +
                "<style>body{margin:0;font-family:\"Helvetica Neue\",\"PingFang SC\",sans-serif;background:#f7f3ee;color:#1d2a35;} .wrap{max-width:760px;margin:0 auto;padding:28px 18px 40px;} .card{background:#fffdf8;border:1px solid #dccdbd;border-radius:22px;padding:24px;box-shadow:0 16px 44px rgba(76,51,33,0.08);} input{width:100%;padding:11px 12px;border:1px solid #cebba8;border-radius:12px;box-sizing:border-box;margin-bottom:12px;} button{border:0;border-radius:12px;background:#8c4b2f;color:#fff;padding:11px 18px;cursor:pointer;} code{font-family:Consolas,monospace;} p{line-height:1.75;}</style></head>" +
                "<body><div class=\"wrap\"><div class=\"card\"><h1>Login</h1><p>参考官方 samples/web，提交目标就是 <code>/login.jsp</code>。</p>" +
                errorBlock +
                "<form method=\"post\" action=\"/login.jsp\"><label>Username</label><input name=\"username\" value=\"root\" />" +
                "<label>Password</label><input type=\"password\" name=\"password\" value=\"secret\" />" +
                "<label><input type=\"checkbox\" name=\"rememberMe\" value=\"true\" checked style=\"width:auto;\"> Remember Me</label><br/><br/>" +
                "<button type=\"submit\">Login</button></form></div></div></body></html>";
    }

    @GetMapping(value = "/home.jsp", produces = "text/html;charset=UTF-8")
    public String home() {
        Subject subject = SecurityUtils.getSubject();
        String principal = subject.getPrincipal() == null ? "anonymous" : subject.getPrincipal().toString();
        return "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>Home</title>" +
                "<style>body{margin:0;font-family:\"Helvetica Neue\",\"PingFang SC\",sans-serif;background:#f7f3ee;color:#1d2a35;} .wrap{max-width:920px;margin:0 auto;padding:28px 18px 40px;} .card{background:#fffdf8;border:1px solid #dccdbd;border-radius:22px;padding:24px;box-shadow:0 16px 44px rgba(76,51,33,0.08);} code{font-family:Consolas,monospace;} p{line-height:1.75;} a{color:#145b52;}</style></head>" +
                "<body><div class=\"wrap\"><div class=\"card\"><h1>RememberMe Protected Resource</h1>" +
                "<p>当前主体：<code>" + principal + "</code></p>" +
                "<p>authenticated=<code>" + subject.isAuthenticated() + "</code>, remembered=<code>" + subject.isRemembered() + "</code></p>" +
                "<p>这个页面由 Shiro <code>user</code> 规则保护，既允许已认证用户进入，也允许 RememberMe 恢复的用户进入。</p>" +
                "<p>如果请求回到了登录页并带有 <code>rememberMe=deleteMe</code>，说明当前 Cookie 走到了错误处理路径；如果还能正常进入这个页面，则说明它更接近可通过处理链的路径。</p>" +
                "<p><a href=\"/account/index.jsp\">Account Page</a></p>" +
                "<p><a href=\"/logout\">退出</a></p></div></div></body></html>";
    }

    @GetMapping(value = "/account/index.jsp", produces = "text/html;charset=UTF-8")
    public String accountIndex() {
        Subject subject = SecurityUtils.getSubject();
        return "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>Account</title>" +
                "<style>body{margin:0;font-family:\"Helvetica Neue\",\"PingFang SC\",sans-serif;background:#f7f3ee;color:#1d2a35;} .wrap{max-width:920px;margin:0 auto;padding:28px 18px 40px;} .card{background:#fffdf8;border:1px solid #dccdbd;border-radius:22px;padding:24px;box-shadow:0 16px 44px rgba(76,51,33,0.08);} code{font-family:Consolas,monospace;} p{line-height:1.75;} a{color:#145b52;}</style></head>" +
                "<body><div class=\"wrap\"><div class=\"card\"><h1>Account Page</h1>" +
                "<p>当前主体：<code>" + subject.getPrincipal() + "</code></p>" +
                "<p>这是一个仅登录用户可访问的页面，用来贴近 samples/web 的受保护路径。</p>" +
                "<p><a href=\"/home.jsp\">Home</a></p><p><a href=\"/logout\">Logout</a></p></div></div></body></html>";
    }

    @GetMapping(value = "/oracle/info", produces = "application/json;charset=UTF-8")
    public Map<String, Object> oracleInfo() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("title", "Apache Shiro 1.2.5-1.4.1 Padding Oracle 靶场");
        result.put("cve", "CVE-2019-12422");
        result.put("shiroVersion", "1.4.1");
        result.put("runtime", "spring-boot");
        result.put("loginUrl", "/login.jsp");
        result.put("protectedUrl", "/home.jsp");
        result.put("accountUrl", "/account/index.jsp");
        result.put("affectedVersions", Arrays.asList("1.2.5", "1.2.6", "1.3.0", "1.3.1", "1.3.2", "1.4.0-RC2", "1.4.0", "1.4.1"));
        result.put("fixedVersion", "1.4.2");
        result.put("notes", Arrays.asList(
                "先访问 /login.jsp 并勾选 Remember Me，记录 rememberMe Cookie。",
                "随后携带 rememberMe Cookie 访问 /home.jsp 或 /account/index.jsp，观察响应头是否出现 rememberMe=deleteMe。",
                "过滤链改成了更贴近官方 samples/web 的 IniShiroFilter + shiro.ini 形式。"
        ));
        return result;
    }

    @GetMapping(value = "/health", produces = "text/plain;charset=UTF-8")
    public String health() {
        return "ok";
    }
}
