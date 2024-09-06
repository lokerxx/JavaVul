package com.myapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UnauthorizedAccessController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String userLoginDataFilePath = "result/userLoginData.json"; // 定义用户登录数据的文件路径

    // 方法 1: 通过请求参数查询用户信息
    @GetMapping("/api/user")
    public Map<String, Object> getUserById(@RequestParam("id") int id) {
        List<Map<String, Object>> users = readUserData();
        if (users != null) {
            Optional<Map<String, Object>> user = users.stream()
                    .filter(u -> u.get("id").equals(id))
                    .findFirst();
            if (user.isPresent()) {
                return user.get();
            }
        }
        return null;
    }

    // 方法 2: 通过cookie查询用户信息
    @GetMapping("/api/user/cookie")
    public Map<String, Object> getUserByCookie(@RequestHeader("Cookie") String cookieHeader) {
        String idCookie = extractIdFromCookie(cookieHeader);
        if (idCookie != null) {
            try {
                int id = Integer.parseInt(idCookie);
                return getUserById(id); // 调用上面的方法查询用户
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 从cookie中提取ID
    private String extractIdFromCookie(String cookieHeader) {
        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String[] keyValue = cookie.trim().split("=");
            if (keyValue.length == 2 && keyValue[0].equals("id")) {
                return keyValue[1];
            }
        }
        return null;
    }

    // 读取用户数据
    private List<Map<String, Object>> readUserData() {
        File file = new File(userLoginDataFilePath);
        try {
            return objectMapper.readValue(file, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
