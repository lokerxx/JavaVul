package com.myapp.servicea.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class ServiceAController {
    private static final Logger logger = LoggerFactory.getLogger(ServiceAController.class);

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/call-service-b")
    public String callServiceB() {
        return restTemplate.getForObject("http://service-b/some-endpoint", String.class);
    }

    @GetMapping("/process-user-data")
    public String processUserData(@RequestParam String userData) {
        // 发送数据到Service B
        restTemplate.postForObject("http://service-b/process", userData, String.class);
        return "Data processing started";
    }

    @PostMapping("/receiveAuditResult")
    public ResponseEntity<String> receiveAuditResult(@RequestBody String auditResult) {
        // 处理接收到的审核结果
        // 这里可以记录结果，或者根据结果采取进一步的行动
        logger.info("auditResult:"+auditResult);
        return ResponseEntity.ok("Audit result received: " + auditResult);
    }





}