package myapp;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FastjsonController {


    @PostMapping("/fastjson1.2.80-process")
    public String fastjson1_2_80_process(@RequestBody String data) {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            // 处理 jsonObject
            return "Processed: " + jsonObject;
        } catch (JSONException e) {
            // 处理JSON解析异常
            return "JSON解析异常: " + e.getMessage();
        } catch (RuntimeException e) {
            // 处理运行时异常
            return "运行时异常: " + e.getMessage();
        }
    }
}
