package com.myapp.controller;

import com.myapp.config.ShiroConfig;
import com.myapp.support.ShiroWeakKeySupport;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
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
                "      <p>当前靶场弱密钥（运行时真实使用的固定公开值）：<span class=\"mono\">" + ShiroConfig.WEAK_PUBLIC_KEY + "</span></p>\n" +
                "    </div>\n" +
                "    <div class=\"card\">\n" +
                "      <h3>弱密钥状态</h3>\n" +
                "      <div class=\"row\">\n" +
                "        <button type=\"button\" onclick=\"loadCheck()\">检测当前密钥配置</button>\n" +
                "        <button type=\"button\" onclick=\"startScan()\">开始遍历 Key</button>\n" +
                "        <button type=\"button\" onclick=\"loadDictionary()\">显示脚本 Key 列表</button>\n" +
                "      </div>\n" +
                "      <pre id=\"statusBox\">等待检测...</pre>\n" +
                "      <p><strong>当前 rememberMe key</strong></p>\n" +
                "      <pre id=\"currentKey\">-</pre>\n" +
                "      <p><strong>遍历进度</strong></p>\n" +
                "      <pre id=\"scanBox\">等待开始...</pre>\n" +
                "      <p><strong>脚本 Key 字典</strong></p>\n" +
                "      <pre id=\"dictionary\">点击“显示脚本 Key 列表”后加载完整字典。</pre>\n" +
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
                "    async function loadCheck(){\n" +
                "      const res = await fetch('/rememberme/check');\n" +
                "      const data = await res.json();\n" +
                "      document.getElementById('currentKey').textContent = data.currentKey || '(unavailable)';\n" +
                "      document.getElementById('statusBox').textContent =\n" +
                "        'Shiro版本: ' + data.shiroVersion + '\\n' +\n" +
                "        '是否命中脚本字典: ' + data.inDictionary + '\\n' +\n" +
                "        '是否默认 key: ' + data.isDefaultKey + '\\n' +\n" +
                "        '字典总数: ' + data.dictionarySize + '\\n' +\n" +
                "        '命中下标: ' + (data.matchIndex === null ? '-' : data.matchIndex) + '\\n' +\n" +
                "        '运行时真实使用该 key: ' + data.runtimeWeakKeyMode + '\\n' +\n" +
                "        '说明: ' + data.message;\n" +
                "    }\n" +
                "    async function loadDictionary(){\n" +
                "      const res = await fetch('/rememberme/dictionary');\n" +
                "      const data = await res.json();\n" +
                "      document.getElementById('dictionary').textContent = data.keys.join('\\n');\n" +
                "    }\n" +
                "    async function startScan(){\n" +
                "      const scanBox = document.getElementById('scanBox');\n" +
                "      const res = await fetch('/rememberme/scan');\n" +
                "      const data = await res.json();\n" +
                "      const keys = data.keys || [];\n" +
                "      const maxPreview = Math.min(keys.length, 30);\n" +
                "      scanBox.textContent = '正在遍历 ' + data.dictionarySize + ' 个 key...';\n" +
                "      for (let i = 0; i < maxPreview; i++) {\n" +
                "        scanBox.textContent = '正在检测 [' + i + '] ' + keys[i];\n" +
                "        await new Promise(resolve => setTimeout(resolve, 30));\n" +
                "      }\n" +
                "      if (data.matchIndex !== null) {\n" +
                "        scanBox.textContent = '在下标 ' + data.matchIndex + ' 命中 key\\n' + data.matchedKey + '\\n\\n已检测数量：' + data.checkedCount;\n" +
                "      } else {\n" +
                "        scanBox.textContent = '脚本字典中未命中当前 key。\\n已检测数量：' + data.checkedCount;\n" +
                "      }\n" +
                "      document.getElementById('currentKey').textContent = data.currentKey || '(unavailable)';\n" +
                "    }\n" +
                "    loadCheck();\n" +
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

    @GetMapping(value = {"/weak-key/status", "/rememberme/check"}, produces = "application/json;charset=UTF-8")
    public Map<String, Object> weakKeyStatus() {
        String currentKey = ShiroWeakKeySupport.extractCurrentKeyBase64(rememberMeManager);
        List<String> dictionary = ShiroWeakKeySupport.SCRIPT_KEYS;
        boolean inDictionary = currentKey != null && dictionary.contains(currentKey);
        boolean isDefaultKey = ShiroWeakKeySupport.DEFAULT_KEY.equals(currentKey);
        Integer matchIndex = currentKey == null ? null : dictionary.indexOf(currentKey);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("shiroVersion", "1.8.0");
        result.put("currentKey", currentKey);
        result.put("runtimeWeakKeyMode", true);
        result.put("isWeakConfigured", inDictionary);
        result.put("inDictionary", inDictionary);
        result.put("isDefaultKey", isDefaultKey);
        result.put("matchIndex", matchIndex >= 0 ? matchIndex : null);
        result.put("dictionarySize", dictionary.size());
        result.put("message", buildMessage(currentKey, inDictionary, isDefaultKey));
        return result;
    }

    @GetMapping(value = "/rememberme/dictionary", produces = "application/json;charset=UTF-8")
    public Map<String, Object> rememberMeDictionary() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("keys", ShiroWeakKeySupport.SCRIPT_KEYS);
        return result;
    }

    @GetMapping(value = "/rememberme/scan", produces = "application/json;charset=UTF-8")
    public Map<String, Object> rememberMeScan() {
        String currentKey = ShiroWeakKeySupport.extractCurrentKeyBase64(rememberMeManager);
        List<String> dictionary = ShiroWeakKeySupport.SCRIPT_KEYS;
        int checkedCount = 0;
        Integer matchIndex = null;
        String matchedKey = null;

        for (int i = 0; i < dictionary.size(); i++) {
            checkedCount++;
            String candidate = dictionary.get(i);
            if (candidate.equals(currentKey)) {
                matchIndex = i;
                matchedKey = candidate;
                break;
            }
        }

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("currentKey", currentKey);
        result.put("checkedCount", checkedCount);
        result.put("dictionarySize", dictionary.size());
        result.put("matchIndex", matchIndex);
        result.put("matchedKey", matchedKey);
        result.put("keys", dictionary);
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

    private String buildMessage(String currentKey, boolean inDictionary, boolean isDefaultKey) {
        if (currentKey == null) {
            return "无法从 CookieRememberMeManager 中提取当前 rememberMe key。";
        }
        if (isDefaultKey) {
            return "当前 rememberMe key 就是 Shiro 默认 key，并且存在于脚本字典中。";
        }
        if (inDictionary) {
            return "当前 rememberMe key 命中了脚本字典中的弱 key。";
        }
        return "当前 rememberMe key 已成功提取，且没有出现在脚本字典中。";
    }
}
