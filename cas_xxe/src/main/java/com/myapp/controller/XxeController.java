package com.myapp.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.jasig.cas.client.util.XmlUtils;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


@RestController
public class XxeController {

    @PostMapping("/xxe_cas")
    public String xxe_cas(@RequestBody String xmlData) throws Exception {
        System.out.println(xmlData);
        try {
            // 假设 getTextForElement 方法是可访问的并且适用。你可能需要根据实际情况调整此调用。
            // 此处的 "elementName" 应替换为你希望从XML中提取的元素的名称。
            String elementName = "userInfo";
            String content = XmlUtils.getTextForElement(xmlData, elementName);
            System.out.println(content);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing XML";
        }
    }


}
