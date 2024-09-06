package com.myapp.controller;

import com.github.javafaker.Faker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
public class FakePersonDataController {
    // 设置Faker使用中文（中国）语言环境
    private final Faker faker = new Faker(new Locale("zh-CN"));
    private final Random random = new Random();

    // 辅助方法：生成中国风格的身份证号码
    private String generateChineseIDNumber() {
        // 地址码（随机6位）
        String addressCode = String.format("%06d", random.nextInt(1000000));
        // 出生日期（随机8位，假设为1980-2022年间）
        int year = 1980 + random.nextInt(43); // 随机生成年份（1980-2022）
        int month = 1 + random.nextInt(12); // 随机生成月份
        int day = 1 + random.nextInt(28); // 随机生成日期（简单化处理为最多28天）
        String birthDate = String.format("%04d%02d%02d", year, month, day);
        // 顺序码（随机3位）
        String orderCode = String.format("%03d", random.nextInt(1000));
        // 校验码（随机1位）
        char checkCode = (char) ('0' + random.nextInt(10)); // 简化为0-9

        return addressCode + birthDate + orderCode + checkCode;
    }


    // 接口 1: 生成姓名、身份证、手机号码（返回JSON格式）
    @GetMapping("/api/fake-person-basic")
    public Map<String, String> generateFakePersonBasic() {
        Map<String, String> personInfo = new HashMap<>();
        personInfo.put("name", faker.name().fullName());
        personInfo.put("idNumber", generateChineseIDNumber());
        personInfo.put("phoneNumber", faker.phoneNumber().cellPhone());
        return personInfo;
    }

    // 接口 2: 生成姓名、身份证、手机号码、地址、性别、电子邮箱地址、电话号码（返回JSON格式）
    @GetMapping("/api/fake-person-full")
    public Map<String, String> generateFakePersonFull() {
        Map<String, String> personInfo = new HashMap<>();
        personInfo.put("name", faker.name().fullName());
        personInfo.put("idNumber", generateChineseIDNumber());
        personInfo.put("phoneNumber", faker.phoneNumber().cellPhone());
        personInfo.put("address", faker.address().fullAddress());
        personInfo.put("gender", faker.options().option("男", "女"));  // 随机生成性别
        personInfo.put("email", faker.internet().emailAddress()); // 生成电子邮箱地址
        personInfo.put("landlineNumber", faker.phoneNumber().phoneNumber()); // 生成座机电话号码
        return personInfo;
    }

    // 新增接口 1: 随机生成10-100个基本个人信息
    @GetMapping("/api/fake-person-basic-list")
    public List<Map<String, String>> generateFakePersonBasicList() {
        int count = 10 + random.nextInt(91); // 随机生成10到100之间的数字
        List<Map<String, String>> personList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            personList.add(generateFakePersonBasic());
        }
        return personList;
    }

    // 新增接口 2: 随机生成10-100个完整的个人信息
    @GetMapping("/api/fake-person-full-list")
    public List<Map<String, String>> generateFakePersonFullList() {
        int count = 10 + random.nextInt(91); // 随机生成10到100之间的数字
        List<Map<String, String>> personList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            personList.add(generateFakePersonFull());
        }
        return personList;
    }

}
