package com.myapp.controller;

import com.github.javafaker.Faker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Controller
public class IndexController {

    private final Faker faker = new Faker(new Locale("zh-CN"));
    Faker englishFaker = new Faker(new Locale("en-US")); // 使用英文环境的Faker实例
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper(); // 创建 Jackson ObjectMapper 对象
    private int idCounter = 1; // ID 计数器

    @GetMapping("/index")
    public String showVulnerabilityPage() {
        initializeFakeData(); // 访问index页面时生成初始化虚假数据
        return "index"; // 返回页面名称，不需要添加".html"
    }

    // 初始化虚假数据并保存到文件
    private void initializeFakeData() {
        String personalDataFilePath = "result/personalData.json";
        String ecommerceDataFilePath = "result/ecommerceData.json";
        String userLoginDataFilePath = "result/userLoginData.json"; // 用户登录信息文件路径

        // 检查文件是否存在，如果存在则跳过写入
        if (!fileExists(personalDataFilePath)) {
            List<Map<String, Object>> personalDataList = new ArrayList<>();
            // 生成10个个人数据
            for (int i = 0; i < 1000; i++) {
                personalDataList.add(generateFakePerson());
            }
            saveDataToFile(personalDataFilePath, personalDataList);
        }

        if (!fileExists(ecommerceDataFilePath)) {
            List<Map<String, Object>> ecommerceDataList = new ArrayList<>();
            // 生成10个电商订单数据
            for (int i = 0; i < 1000; i++) {
                ecommerceDataList.add(generateEcommerceOrder());
            }
            saveDataToFile(ecommerceDataFilePath, ecommerceDataList);
        }

        if (!fileExists(userLoginDataFilePath)) {
            List<Map<String, Object>> userLoginDataList = new ArrayList<>();
            // 生成10个用户登录数据
            for (int i = 0; i < 1000; i++) {
                userLoginDataList.add(generateUserLoginData());
            }
            saveDataToFile(userLoginDataFilePath, userLoginDataList);
        }
    }

    // 辅助方法：检查文件是否存在
    private boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    // 辅助方法：生成单个个人的敏感信息，包含自增ID
    private Map<String, Object> generateFakePerson() {
        Map<String, Object> personInfo = new HashMap<>();
        personInfo.put("id", idCounter++); // 添加自增ID
        personInfo.put("name", faker.name().fullName());
        personInfo.put("email", faker.internet().emailAddress()); // 生成电子邮箱地址

        personInfo.put("idNumber", generateChineseIDNumber());
        personInfo.put("phoneNumber", faker.phoneNumber().cellPhone());
        personInfo.put("address", faker.address().fullAddress());
        personInfo.put("gender", faker.options().option("男", "女"));
        personInfo.put("email", faker.internet().emailAddress());
        personInfo.put("landlineNumber", faker.phoneNumber().phoneNumber());
        return personInfo;
    }

    // 辅助方法：生成单个电商订单的敏感信息，包含自增ID
    private Map<String, Object> generateEcommerceOrder() {
        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("id", idCounter++); // 添加自增ID
        orderInfo.put("orderNumber", generateOrderNumber());
        orderInfo.put("name", faker.name().fullName());
        orderInfo.put("phoneNumber", faker.phoneNumber().cellPhone());
        orderInfo.put("address", faker.address().fullAddress());
        orderInfo.put("productInfo", generateProductInfo());
        return orderInfo;
    }

    // 新增辅助方法：生成用户登录信息，包含自增ID和username
    private Map<String, Object> generateUserLoginData() {
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", idCounter++); // 添加自增ID
        loginInfo.put("name", faker.name().fullName()); // 中文全名
        loginInfo.put("username", englishFaker.name().username()); // 使用英文环境生成英文用户名
        loginInfo.put("email", faker.internet().emailAddress());
        loginInfo.put("phoneNumber", faker.phoneNumber().cellPhone());
        loginInfo.put("userRole", faker.options().option("管理员", "用户", "访客")); // 随机生成用户角色
        loginInfo.put("password", faker.internet().password(8, 16)); // 生成8到16字符的随机密码
        return loginInfo;
    }

    // 辅助方法：生成随机订单号
    private String generateOrderNumber() {
        return String.format("%010d", random.nextInt(1000000000));
    }

    // 辅助方法：生成随机商品信息
    private Map<String, Object> generateProductInfo() {
        Map<String, Object> productInfo = new HashMap<>();
        productInfo.put("productName", faker.commerce().productName());
        productInfo.put("quantity", 1 + random.nextInt(5)); // 随机生成1到5之间的商品数量
        productInfo.put("price", Double.parseDouble(faker.commerce().price())); // 生成随机价格
        return productInfo;
    }

    // 辅助方法：生成中国风格的身份证号码
    private String generateChineseIDNumber() {
        String addressCode = String.format("%06d", random.nextInt(1000000));
        int year = 1980 + random.nextInt(43);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        String birthDate = String.format("%04d%02d%02d", year, month, day);
        String orderCode = String.format("%03d", random.nextInt(1000));
        char checkCode = (char) ('0' + random.nextInt(10));
        return addressCode + birthDate + orderCode + checkCode;
    }

    // 辅助方法：将数据保存到文件 (JSON 格式)
    private void saveDataToFile(String filePath, List<?> data) {
        File file = new File(filePath);
        file.getParentFile().mkdirs(); // 创建父目录

        // 输出文件的绝对路径
        System.out.println("Saving data to file: " + file.getAbsolutePath());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // 使用 ObjectMapper 将数据转换为 JSON 格式字符串
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            writer.write(json); // 写入 JSON 格式的数据
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
