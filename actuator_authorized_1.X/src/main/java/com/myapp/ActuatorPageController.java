package com.myapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActuatorPageController {

    @GetMapping(value = {"/", "/actuator-authorized-1x"}, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String index() {
        return "<!DOCTYPE html>" +
                "<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>Actuator 1.X 修复测试页</title>" +
                style() +
                "</head><body><div class=\"container\"><h1>Actuator 1.X 修复测试页</h1>" +
                "<p class=\"hint\">这个页面用于验证修复效果。默认访问当前服务的 <code>/trace</code>，你也可以手动改目标路径再发送。</p>" +
                "<label for=\"path\">目标路径</label><input id=\"path\" value=\"/trace\" />" +
                "<div class=\"actions\"><button class=\"fill\" onclick=\"fillTrace()\">填充 /trace</button><button class=\"send\" onclick=\"sendCurrent()\">发送当前请求</button></div>" +
                "<label for=\"result\">响应结果</label><pre id=\"result\">等待发送请求...</pre></div>" +
                "<script>const pathBox=document.getElementById('path');const resultBox=document.getElementById('result');function fillTrace(){pathBox.value='/trace';}async function sendCurrent(){resultBox.textContent='请求发送中...';try{const response=await fetch(pathBox.value,{method:'GET'});const text=await response.text();resultBox.textContent='HTTP '+response.status+'\\n'+text;}catch(error){resultBox.textContent='请求失败: '+error;}}</script>" +
                "</body></html>";
    }

    private String style() {
        return "<style>body{font-family:Arial,sans-serif;margin:40px;background:#f5f7fb;color:#1f2937;}.container{max-width:920px;margin:0 auto;background:#fff;padding:24px;border-radius:12px;box-shadow:0 10px 30px rgba(0,0,0,0.08);}input,pre{width:100%;box-sizing:border-box;}input{padding:12px;border-radius:8px;border:1px solid #d0d7de;margin-bottom:16px;}pre{min-height:160px;padding:12px;border-radius:8px;background:#111827;color:#e5e7eb;overflow:auto;}.actions{display:flex;gap:12px;margin:20px 0;}button{padding:12px 18px;border:0;border-radius:8px;cursor:pointer;font-size:14px;color:#fff;}.fill{background:#1f6feb;}.send{background:#111827;}.hint{color:#6b7280;font-size:14px;}code{background:#eef2ff;padding:2px 6px;border-radius:4px;}</style>";
    }
}
