package com.myapp.serviced.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class ServiceDController {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDController.class);

    @Autowired
    private RestTemplate restTemplate;



    @PostMapping("/analyze")
    public void analyze(@RequestBody String data) {
        // 执行数据分析
        String analysisResult = performAnalysis(data);

        // 发送结果到Service E
        restTemplate.postForObject("http://service-e/report", analysisResult, Void.class);
    }

    private String performAnalysis(String data) {
        String reversed = reverseString(data);
        String wordFreq = wordFrequencyAnalysis(data);
        String strLength = stringLengthAnalysis(data);
        String replaced = findAndReplace(data, "oldWord", "newWord");
        String caseCount = countCase(data);
        logger.info("reversed:"+reversed);
        logger.info("wordFreq:"+wordFreq);
        logger.info("strLength:"+strLength);
        logger.info("replaced:"+replaced);
        logger.info("caseCount:"+caseCount);

        return "Reversed: " + reversed + "\n" +
                "Word Frequency: " + wordFreq + "\n" +
                "String Length: " + strLength + "\n" +
                "Replaced: " + replaced + "\n" +
                "Case Count: " + caseCount;
    }
//反转字符串
    private String reverseString(String data) {
        return new StringBuilder(data).reverse().toString();
    }
//统计单词频率
    private String wordFrequencyAnalysis(String data) {
        Map<String, Long> wordFrequency = Arrays.stream(data.split("\\s+")) // 分割单词
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return "Word frequency: " + wordFrequency.toString();
    }
    //字符串长度分析
    private String stringLengthAnalysis(String data) {
        return "Length of string: " + data.length();
    }

    //查找和替换特定字符
    private String findAndReplace(String data, String find, String replace) {
        return data.replace(find, replace);
    }

    //统计大写和小写字符数量
    private String countCase(String data) {
        long upperCase = data.chars().filter(Character::isUpperCase).count();
        long lowerCase = data.chars().filter(Character::isLowerCase).count();

        return "Upper case count: " + upperCase + ", Lower case count: " + lowerCase;
    }



}