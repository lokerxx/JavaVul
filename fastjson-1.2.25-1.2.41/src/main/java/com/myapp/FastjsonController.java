package com.myapp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.web.bind.annotation.*;

@RestController
public class FastjsonController {


    @PostMapping("/fastjson1.2.25-process")
    public String fastjson1_2_25_process(@RequestBody String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        // 处理 jsonObject
        return "Processed: " + jsonObject;
    }

    @PostMapping("/fastjson1.2.41-process-setAutoTypeSupport")
    public String fastjson1_2_41_process(@RequestBody String data) {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);

        JSON.parse(data);
        return data;
    }
}
