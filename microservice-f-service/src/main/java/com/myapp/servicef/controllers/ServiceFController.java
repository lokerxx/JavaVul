package com.myapp.servicef.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
public class ServiceFController {

    @Autowired
    private RestTemplate restTemplate;


    @PostMapping("/audit")
    public void auditReport(@RequestBody String reportData) {
        // 审核报告
        boolean isApproved = auditReportData(reportData);

        // 准备发送的消息
        String message = isApproved ? "Approved" : "Rejected";

        // 调用Service A的接口发送审核结果
        restTemplate.postForObject("http://service-a/receiveAuditResult", message, String.class);
    }

    private boolean auditReportData(String reportData) {
        // 步骤1: 检查报告长度
        if (!isLengthValid(reportData)) {
            return false;
        }

        // 步骤2: 检查关键词
        if (!containsKeyWords(reportData, Arrays.asList("Important", "Critical"))) {
            return false;
        }

        // 步骤3: 检查报告格式
        if (!isFormatCorrect(reportData)) {
            return false;
        }

        return true; // 如果所有检查都通过，则审核通过
    }

    private boolean isLengthValid(String data) {
        // 检查报告的长度是否符合预期，例如不少于100个字符
        return data != null && data.length() >= 100;
    }

    private boolean containsKeyWords(String data, List<String> keyWords) {
        // 检查报告是否包含特定的关键词
        return keyWords.stream().allMatch(data::contains);
    }

    private boolean isFormatCorrect(String data) {
        // 检查报告格式是否正确，例如是否以特定字符串开始或结束
        return data.startsWith("Report:") && data.endsWith("End of Report");
    }
}