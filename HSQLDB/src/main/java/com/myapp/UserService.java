package com.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findUsersByUsername(String username) {
        // 故意构建的 SQL 注入漏洞
        return jdbcTemplate.queryForList("SELECT * FROM User WHERE username = '" + username + "'");
    }

    public List<Map<String, Object>> findUsersByUsernameRepair(String username) {
        // 使用参数化查询以避免 SQL 注入
        String sql = "SELECT * FROM User WHERE username = ?";
        return jdbcTemplate.queryForList(sql, new Object[]{username});
    }

}