package com.myapp.controller;

import com.github.javafaker.Faker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ECommerceController {

    private final Faker faker = new Faker(new Locale("zh-CN"));
    private final Random random = new Random();

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

    // 接口 1: 生成单个订单的敏感信息
    @GetMapping("/api/ecommerce-order")
    public Map<String, Object> generateEcommerceOrder() {
        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("orderNumber", generateOrderNumber()); // 生成订单号
        orderInfo.put("name", faker.name().fullName()); // 生成姓名
        orderInfo.put("phoneNumber", faker.phoneNumber().cellPhone()); // 生成手机号
        orderInfo.put("address", faker.address().fullAddress()); // 生成地址
        orderInfo.put("productInfo", generateProductInfo()); // 生成商品信息

        return orderInfo;
    }

    // 接口 2: 随机生成10-100个订单的敏感信息
    @GetMapping("/api/ecommerce-order-list")
    public List<Map<String, Object>> generateEcommerceOrderList() {
        int count = 10 + random.nextInt(91); // 随机生成10到100之间的数字
        List<Map<String, Object>> orderList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            orderList.add(generateEcommerceOrder());
        }
        return orderList;
    }
}
