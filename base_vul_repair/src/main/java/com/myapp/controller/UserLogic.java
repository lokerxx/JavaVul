package com.myapp.controller;

import com.myapp.model.User;
import com.myapp.model.User2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service  // 将 UserLogic 标记为 Spring 管理的服务组件
public class UserLogic {

    private final JdbcTemplate jdbcTemplate;

    public UserLogic(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getUser(String name) {
        String query = "SELECT * FROM users WHERE name = ?";
        return jdbcTemplate.query(query, new Object[]{name}, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setName(rs.getString("name"));
                // 设置其他属性，根据您 User 类的定义
                return user;
            }
        });
    }

    public List<User2> getUser2(User2 user) {
        // 使用参数化查询防止 SQL 注入
        String query = "SELECT * FROM users WHERE name = ?";

        // 执行查询并返回结果
        return jdbcTemplate.query(query, new Object[]{user.getName()}, new RowMapper<User2>() {
            @Override
            public User2 mapRow(ResultSet rs, int rowNum) throws SQLException {
                User2 retrievedUser = new User2();
                retrievedUser.setName(rs.getString("name"));
                // 设置其他属性
                return retrievedUser;
            }
        });
    }

}