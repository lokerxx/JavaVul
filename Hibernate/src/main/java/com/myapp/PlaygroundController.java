package com.myapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaygroundController {

    @GetMapping(value = {"/", "/playground"}, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String index() {
        String attackPath = "/Hibernate_injection?username=test%27%20OR%20%271%27%3D%271";
        String repairPath = "/Hibernate_injection_repair?username=test%27%20OR%20%271%27%3D%271";
        return "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>Hibernate Playground</title>" + style() +
                "</head><body><div class=\"container\"><h1>Hibernate Playground</h1><p class=\"hint\">默认 payload 使用 <code>test' OR '1'='1</code>，这样更容易看到漏洞版返回多条、修复版返回空结果的差异。</p>" +
                panel("attack","Hibernate attack",attackPath) +
                panel("repair","Hibernate repair",repairPath) +
                "</div><script>" +
                "async function send(path,id){const result=document.getElementById('result-'+id);result.textContent='请求发送中...';try{const response=await fetch(document.getElementById('path-'+id).value,{method:'GET'});const text=await response.text();result.textContent='HTTP '+response.status+'\\n'+text;}catch(error){result.textContent='请求失败: '+error;}}" +
                "</script></body></html>";
    }

    private String panel(String id, String title, String path) {
        return "<div class=\"panel\"><h2>" + title + "</h2><label for=\"path-" + id + "\">Path</label><input id=\"path-" + id + "\" value=\"" + path + "\" /><div class=\"actions\"><button class=\"send\" onclick=\"send(document.getElementById('path-" + id + "').value,'" + id + "')\">发送当前请求</button></div><pre id=\"result-" + id + "\">等待发送请求...</pre></div>";
    }

    private String style() {
        return "<style>body{font-family:Arial,sans-serif;margin:40px;background:#f5f7fb;color:#1f2937}.container{max-width:980px;margin:0 auto;background:#fff;padding:24px;border-radius:12px;box-shadow:0 10px 30px rgba(0,0,0,.08)}.panel{border:1px solid #d0d7de;border-radius:12px;padding:18px;margin-top:20px}input,pre{width:100%;box-sizing:border-box}input{padding:12px;border-radius:8px;border:1px solid #d0d7de;margin-bottom:16px}pre{min-height:160px;padding:12px;border-radius:8px;background:#111827;color:#e5e7eb;overflow:auto}.actions{display:flex;gap:12px;margin:20px 0}button{padding:12px 18px;border:0;border-radius:8px;cursor:pointer;font-size:14px;color:#fff}.send{background:#111827}.hint{color:#6b7280;font-size:14px}</style>";
    }
}
