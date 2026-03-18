package com.myapp.controller;

import com.myapp.config.ShiroConfig;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class Shiro180Controller {

    private final CookieRememberMeManager rememberMeManager;

    public Shiro180Controller(CookieRememberMeManager rememberMeManager) {
        this.rememberMeManager = rememberMeManager;
    }

    @GetMapping(value = {"/", "/login-page", "/shiro-1.8.0"}, produces = "text/html;charset=UTF-8")
    public String index() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Shiro 1.8.0 弱密钥靶场</title>\n" +
                "  <style>\n" +
                "    body{font-family:Arial,sans-serif;background:#f5f7fb;margin:0;padding:28px 14px;color:#1f2937}\n" +
                "    .wrap{max-width:1050px;margin:0 auto}\n" +
                "    .card{background:#fff;border:1px solid #d6dfeb;border-radius:14px;padding:20px;margin-bottom:16px;box-shadow:0 10px 24px rgba(15,23,42,.06)}\n" +
                "    .row{display:flex;gap:10px;flex-wrap:wrap}\n" +
                "    input,button{padding:10px;border-radius:10px;font-size:14px}\n" +
                "    input{border:1px solid #c7d3e3;min-width:220px}\n" +
                "    button{border:0;background:#0f172a;color:#fff;cursor:pointer}\n" +
                "    pre{background:#0f172a;color:#e2e8f0;border-radius:10px;padding:12px;white-space:pre-wrap;word-break:break-word}\n" +
                "    .pill{display:inline-block;padding:4px 10px;border-radius:999px;background:#eef2ff;color:#312e81;margin-right:8px;margin-bottom:6px}\n" +
                "    .mono{font-family:Consolas,monospace;word-break:break-all}\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"wrap\">\n" +
                "    <div class=\"card\">\n" +
                "      <h2>Shiro 1.8.0 弱密钥集成靶场</h2>\n" +
                "      <p>这个模块用于演示：即使 Shiro 升级到高版本，如果应用仍把 rememberMe 密钥配置成公开弱值，风险依然存在。这属于应用集成/配置问题，而不是官方历史漏洞版本范围变化。</p>\n" +
                "      <span class=\"pill\">Shiro 版本: 1.8.0</span>\n" +
                "      <span class=\"pill\">rememberMe 模式: AES-GCM（高版本默认）</span>\n" +
                "      <p>当前靶场弱密钥（固定公开值）：<span class=\"mono\">" + ShiroConfig.WEAK_PUBLIC_KEY + "</span></p>\n" +
                "    </div>\n" +
                "    <div class=\"card\">\n" +
                "      <h3>弱密钥状态</h3>\n" +
                "      <div class=\"row\">\n" +
                "        <button type=\"button\" onclick=\"checkWeakKey()\">检测当前密钥配置</button>\n" +
                "      </div>\n" +
                "      <pre id=\"statusBox\">等待检测...</pre>\n" +
                "    </div>\n" +
                "    <div class=\"card\">\n" +
                "      <h3>RememberMe 登录验证</h3>\n" +
                "      <form action=\"/login\" method=\"post\">\n" +
                "        <div class=\"row\">\n" +
                "          <input name=\"username\" value=\"admin\" placeholder=\"用户名\" />\n" +
                "          <input type=\"password\" name=\"password\" value=\"admin123\" placeholder=\"密码\" />\n" +
                "          <label><input type=\"checkbox\" name=\"rememberMe\" value=\"true\" checked /> 记住我</label>\n" +
                "        </div>\n" +
                "        <div class=\"row\" style=\"margin-top:10px\">\n" +
                "          <button type=\"submit\">登录</button>\n" +
                "          <button type=\"button\" onclick=\"window.location.href='/profile'\">查看 /profile</button>\n" +
                "          <button type=\"button\" onclick=\"window.location.href='/admin'\">查看 /admin</button>\n" +
                "          <button type=\"button\" onclick=\"window.location.href='/logout'\">退出 /logout</button>\n" +
                "        </div>\n" +
                "      </form>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "  <script>\n" +
                "    async function checkWeakKey(){\n" +
                "      const res = await fetch('/weak-key/status');\n" +
                "      const data = await res.json();\n" +
                "      document.getElementById('statusBox').textContent =\n" +
                "        'Shiro版本: ' + data.shiroVersion + '\\n' +\n" +
                "        '当前rememberMe密钥: ' + data.currentKey + '\\n' +\n" +
                "        '是否公开弱值: ' + data.isWeakConfigured + '\\n' +\n" +
                "        '说明: ' + data.message;\n" +
                "    }\n" +
                "  </script>\n" +
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

    @GetMapping(value = "/weak-key/status", produces = "application/json;charset=UTF-8")
    public Map<String, Object> weakKeyStatus() {
        String currentKey = extractCipherKeyBase64(rememberMeManager);
        boolean weak = ShiroConfig.WEAK_PUBLIC_KEY.equals(currentKey);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("shiroVersion", "1.8.0");
        result.put("currentKey", currentKey);
        result.put("isWeakConfigured", weak);
        result.put("message", weak
                ? "高版本 + 固定公开弱密钥，风险仍然存在（不安全配置）。"
                : "当前密钥不是预置弱值。");
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

    private String extractCipherKeyBase64(CookieRememberMeManager manager) {
        Object key = readField(manager, "encryptionCipherKey");
        if (!(key instanceof byte[])) {
            key = readField(manager, "decryptionCipherKey");
        }
        if (key instanceof byte[]) {
            return Base64.getEncoder().encodeToString((byte[]) key);
        }
        return "unknown";
    }

    private Object readField(Object target, String fieldName) {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException ex) {
                type = type.getSuperclass();
            } catch (IllegalAccessException ex) {
                return null;
            }
        }
        return null;
    }
}
