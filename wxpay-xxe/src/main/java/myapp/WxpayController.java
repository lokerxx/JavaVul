package myapp;

import com.github.wxpay.sdk.WXPayUtil;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WxpayController {


    @PostMapping("/wxpay-xxe")
    public String wxpay_xxe(@RequestBody String xmlData) {
        try {

            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlData);
            System.out.println(resultMap);
            return resultMap.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing request";
        }
    }
}
