package com.myapp.controller;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringWriter;

@RestController
public class SstiController {

    @RequestMapping("/ssti_velocity")
    public String  Velocity(@RequestParam(name = "content") String content) throws Exception {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
        engine.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.setProperty(RuntimeConstants.VM_PERM_ALLOW_INLINE_REPLACE_GLOBAL, true);
        engine.setProperty(RuntimeConstants.VM_LIBRARY, "");
        engine.init();

        // 过滤用户输入，只允许输入字母、数字、下划线和空格
        content = content.replaceAll("[^\\w\\s]", "");

        VelocityContext context = new VelocityContext();

        context.put("username", "test");

        StringWriter stringWriter = new StringWriter();
        engine.evaluate(context, stringWriter, "test", content);
        return stringWriter.toString();
    }
}