package com.myapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaygroundController {

    @GetMapping(value = {"/", "/playground"}, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String index() {
        return "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>druid_authorized Playground</title>" + style() +
                "</head><body><div class=\"container\"><h1>druid_authorized Playground</h1><p class=\"hint\">默认会使用配置中的账号密码访问 <code>/druid</code>。你也可以修改路径、用户名和密码后再发送。</p>" +
                "<label for=\"path\">Path</label><input id=\"path\" value=\"/druid\" />" +
                "<label for=\"username\">Username</label><input id=\"username\" value=\"admin\" />" +
                "<label for=\"password\">Password</label><input id=\"password\" type=\"password\" value=\"123\" />" +
                "<div class=\"actions\"><button class=\"fill\" onclick=\"fillDruid()\">填充 /druid</button><button class=\"send\" onclick=\"sendCurrent()\">发送当前请求</button></div>" +
                "<label for=\"result\">响应</label><pre id=\"result\">等待发送请求...</pre></div><script>" +
                "const pathBox=document.getElementById('path');const usernameBox=document.getElementById('username');const passwordBox=document.getElementById('password');const resultBox=document.getElementById('result');function fillDruid(){pathBox.value='/druid';}" +
                "async function sendCurrent(){resultBox.textContent='请求发送中...';try{const auth='Basic '+btoa(usernameBox.value+':'+passwordBox.value);const response=await fetch(pathBox.value,{method:'GET',headers:{Authorization:auth}});const text=await response.text();resultBox.textContent='HTTP '+response.status+'\\n'+text;}catch(error){resultBox.textContent='请求失败: '+error;}}" +
                "</script></body></html>";
    }

    private String style() {
        return "<style>body{font-family:Arial,sans-serif;margin:40px;background:#f5f7fb;color:#1f2937}.container{max-width:920px;margin:0 auto;background:#fff;padding:24px;border-radius:12px;box-shadow:0 10px 30px rgba(0,0,0,.08)}input,pre{width:100%;box-sizing:border-box}input{padding:12px;border-radius:8px;border:1px solid #d0d7de;margin-bottom:16px}pre{min-height:180px;padding:12px;border-radius:8px;background:#111827;color:#e5e7eb;overflow:auto}.actions{display:flex;gap:12px;margin:20px 0;flex-wrap:wrap}button{padding:12px 18px;border:0;border-radius:8px;cursor:pointer;font-size:14px;color:#fff}.fill{background:#1f6feb}.send{background:#111827}.hint{color:#6b7280;font-size:14px}code{background:#eef2ff;padding:2px 6px;border-radius:4px}</style>";
    }
}
