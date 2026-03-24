package com.myapp.controller;

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
public class ShiroController {

    private final CookieRememberMeManager rememberMeManager;

    public ShiroController(CookieRememberMeManager rememberMeManager) {
        this.rememberMeManager = rememberMeManager;
    }

    @GetMapping(value = {"/", "/login-page", "/shiro-1.2.4"}, produces = "text/html;charset=UTF-8")
    public String index() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Apache Shiro 1.2.4 测试页</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; background: #f6f8fb; margin: 0; padding: 32px 16px; color: #1f2937; }\n" +
                "        .wrap { max-width: 1040px; margin: 0 auto; }\n" +
                "        .card { background: #fff; border: 1px solid #d8e1ee; border-radius: 14px; padding: 24px; margin-bottom: 20px; box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06); }\n" +
                "        h1, h2 { margin-top: 0; }\n" +
                "        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 16px; }\n" +
                "        label { display: block; font-weight: 600; margin-bottom: 8px; }\n" +
                "        input, textarea { width: 100%; box-sizing: border-box; padding: 12px; border: 1px solid #c5d1e0; border-radius: 10px; font-size: 14px; }\n" +
                "        textarea { min-height: 180px; resize: vertical; font-family: Consolas, monospace; }\n" +
                "        button { background: #111827; color: #fff; border: 0; border-radius: 10px; padding: 12px 18px; cursor: pointer; font-size: 14px; }\n" +
                "        button:hover { background: #1f2937; }\n" +
                "        .row { display: flex; gap: 12px; flex-wrap: wrap; }\n" +
                "        .mono { font-family: Consolas, monospace; word-break: break-all; }\n" +
                "        .status { padding: 12px; border-radius: 10px; background: #eef2ff; border: 1px solid #c7d2fe; margin-top: 12px; }\n" +
                "        .ok { background: #ecfdf5; border-color: #a7f3d0; }\n" +
                "        .warn { background: #fff7ed; border-color: #fdba74; }\n" +
                "        .muted { color: #6b7280; }\n" +
                "        ul { margin-bottom: 0; }\n" +
                "        pre { white-space: pre-wrap; word-break: break-word; background: #0f172a; color: #e2e8f0; border-radius: 12px; padding: 16px; overflow: auto; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"wrap\">\n" +
                "        <div class=\"card\">\n" +
                "            <h1>Apache Shiro 1.2.4 RememberMe 测试页</h1>\n" +
                "            <p>这个靶场保留了 Shiro 1.2.4 的 rememberMe 漏洞配置，并增加了一个本地弱 key 检测面板，检测字典直接来自你提供的 shiro-exploit 脚本。</p>\n" +
                "            <ul>\n" +
                "                <li>演示用户：<span class=\"mono\">user / user123</span></li>\n" +
                "                <li>演示管理员：<span class=\"mono\">admin / admin123</span></li>\n" +
                "                <li>默认 Shiro key：<span class=\"mono\">" + ShiroWeakKeySupport.DEFAULT_KEY + "</span></li>\n" +
                "            </ul>\n" +
                "        </div>\n" +
                "        <div class=\"card\">\n" +
                "            <h2>弱 Key 检测</h2>\n" +
                "            <p class=\"muted\">检测逻辑会从当前运行中的 CookieRememberMeManager 里读取 rememberMe 实际密钥，再和 shiro-exploit 脚本中的完整 key 字典进行比对。</p>\n" +
                "            <div class=\"row\">\n" +
                "                <button type=\"button\" onclick=\"loadCheck()\">检测当前 Key</button>\n" +
                "                <button type=\"button\" onclick=\"startScan()\">开始遍历 Key</button>\n" +
                "                <button type=\"button\" onclick=\"loadDictionary()\">显示脚本 Key 列表</button>\n" +
                "            </div>\n" +
                "            <div id=\"checkBox\" class=\"status\">等待检测...</div>\n" +
                "            <p><strong>当前 rememberMe key</strong></p>\n" +
                "            <pre id=\"currentKey\">-</pre>\n" +
                "            <p><strong>遍历进度</strong></p>\n" +
                "            <pre id=\"scanBox\">等待开始...</pre>\n" +
                "            <p><strong>脚本 Key 字典</strong></p>\n" +
                "            <textarea id=\"dictionary\" readonly placeholder=\"点击“显示脚本 Key 列表”后加载完整字典。\"></textarea>\n" +
                "        </div>\n" +
                "        <div class=\"card\">\n" +
                "            <h2>RememberMe 登录验证</h2>\n" +
                "            <form action=\"/login\" method=\"post\">\n" +
                "                <label>用户名</label>\n" +
                "                <input name=\"username\" value=\"admin\" />\n" +
                "                <label>密码</label>\n" +
                "                <input type=\"password\" name=\"password\" value=\"admin123\" />\n" +
                "                <label><input type=\"checkbox\" name=\"rememberMe\" value=\"true\" checked /> 记住我</label>\n" +
                "                <div class=\"row\">\n" +
                "                    <button type=\"submit\">登录</button>\n" +
                "                    <button type=\"button\" onclick=\"window.location.href='/profile'\">打开 /profile</button>\n" +
                "                    <button type=\"button\" onclick=\"window.location.href='/admin'\">打开 /admin</button>\n" +
                "                    <button type=\"button\" onclick=\"window.location.href='/logout'\">退出登录</button>\n" +
                "                </div>\n" +
                "            </form>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        async function loadCheck() {\n" +
                "            const res = await fetch('/rememberme/check');\n" +
                "            const data = await res.json();\n" +
                "            const box = document.getElementById('checkBox');\n" +
                "            const currentKey = document.getElementById('currentKey');\n" +
                "            currentKey.textContent = data.currentKey || '(unavailable)';\n" +
                "            box.className = 'status ' + (data.inDictionary ? 'warn' : 'ok');\n" +
                "            box.innerHTML =\n" +
                "                '<div><strong>是否命中脚本字典：</strong>' + data.inDictionary + '</div>' +\n" +
                "                '<div><strong>是否默认 key：</strong>' + data.isDefaultKey + '</div>' +\n" +
                "                '<div><strong>字典总数：</strong>' + data.dictionarySize + '</div>' +\n" +
                "                '<div><strong>命中下标：</strong>' + (data.matchIndex === null ? '-' : data.matchIndex) + '</div>' +\n" +
                "                '<div><strong>结果说明：</strong>' + data.message + '</div>';\n" +
                "        }\n" +
                "\n" +
                "        async function loadDictionary() {\n" +
                "            const res = await fetch('/rememberme/dictionary');\n" +
                "            const data = await res.json();\n" +
                "            document.getElementById('dictionary').value = data.keys.join('\\n');\n" +
                "        }\n" +
                "\n" +
                "        async function startScan() {\n" +
                "            const scanBox = document.getElementById('scanBox');\n" +
                "            const res = await fetch('/rememberme/scan');\n" +
                "            const data = await res.json();\n" +
                "            const keys = data.keys || [];\n" +
                "            const maxPreview = Math.min(keys.length, 30);\n" +
                "            scanBox.textContent = '正在遍历 ' + data.dictionarySize + ' 个 key...';\n" +
                "            for (let i = 0; i < maxPreview; i++) {\n" +
                "                scanBox.textContent = '正在检测 [' + i + '] ' + keys[i];\n" +
                "                await new Promise(resolve => setTimeout(resolve, 30));\n" +
                "            }\n" +
                "            if (data.matchIndex !== null) {\n" +
                "                scanBox.textContent = '在下标 ' + data.matchIndex + ' 命中 key\\n' + data.matchedKey + '\\n\\n已检测数量：' + data.checkedCount;\n" +
                "            } else {\n" +
                "                scanBox.textContent = '脚本字典中未命中当前 key。\\n已检测数量：' + data.checkedCount;\n" +
                "            }\n" +
                "            document.getElementById('currentKey').textContent = data.currentKey || '(unavailable)';\n" +
                "        }\n" +
                "\n" +
                "        loadCheck();\n" +
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
            return "登录成功，rememberMe=" + rememberMe + "。你可以访问 /profile 查看当前 subject 状态。";
        } catch (AuthenticationException ex) {
            return "登录失败：" + ex.getMessage();
        }
    }

    @GetMapping(value = "/rememberme/check", produces = "application/json;charset=UTF-8")
    public Map<String, Object> rememberMeCheck() {
        String currentKey = ShiroWeakKeySupport.extractCurrentKeyBase64(rememberMeManager);
        List<String> dictionary = ShiroWeakKeySupport.SCRIPT_KEYS;
        boolean inDictionary = currentKey != null && dictionary.contains(currentKey);
        boolean isDefaultKey = ShiroWeakKeySupport.DEFAULT_KEY.equals(currentKey);
        Integer matchIndex = currentKey == null ? null : dictionary.indexOf(currentKey);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("currentKey", currentKey);
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
