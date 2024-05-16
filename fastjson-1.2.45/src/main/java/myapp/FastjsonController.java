package myapp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.web.bind.annotation.*;

@RestController
public class FastjsonController {


    @PostMapping("/fastjson1.2.45-process")
    public String fastjson1_2_45_process(@RequestBody String data) {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);

        JSONObject jsonObject = JSONObject.parseObject(data);
        // 处理 jsonObject
        return "Processed: " + jsonObject;
    }
}
