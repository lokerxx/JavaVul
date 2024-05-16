package com.myapp.controller;



import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


@RestController
public class SstiController {

    @RequestMapping("/ssti_velocity")
    public String  Velocity(@RequestParam(name = "content") String content) throws Exception {
        Velocity.init();
        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("username", "test");

        StringWriter stringWriter = new StringWriter();
        Velocity.evaluate(velocityContext, stringWriter, "test", content);
        return stringWriter.toString();
    }

    @RequestMapping("/ssti_freemarker")
    public String ssti_freemarker(@RequestParam("templateContent") String templateContent) throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        System.out.println(templateContent);
        // 创建模板对象
        Template template = new Template("name", new StringReader(templateContent), configuration);

        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("username", "passwd");
        StringWriter stringWriter = new StringWriter();
        template.process(rootMap, stringWriter);
        return stringWriter.toString();
    }

}