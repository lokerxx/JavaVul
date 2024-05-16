package com.myapp.controller;

import com.google.common.base.Joiner;
import com.myapp.model.User;
import com.myapp.model.User2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service  // 将 UserLogic 标记为 Spring 管理的服务组件
public class UserLogic {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<User> ROW_MAPPER = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            // 设置其他属性...
            return user;
        }
    };

    public UserLogic(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }




    public List<User> getUser(User user) {
        // 存在 SQL 注入风险的查询
        String query = "SELECT * FROM users WHERE name = '" + user.getName() + "'";

        // 执行查询并返回结果
        return jdbcTemplate.query(query, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User retrievedUser = new User();
                retrievedUser.setName(rs.getString("name"));
                // 根据需要设置其他属性
                return retrievedUser;
            }
        });
    }


    public List<User2> getUser2(User2 user) {
        // 存在 SQL 注入风险的查询
        String query = "SELECT * FROM users WHERE name = '" + user.getName() + "'";

        // 执行查询并返回结果
        return jdbcTemplate.query(query, new RowMapper<User2>() {
            @Override
            public User2 mapRow(ResultSet rs, int rowNum) throws SQLException {
                User2 retrievedUser = new User2();
                retrievedUser.setName(rs.getString("name"));
                // 设置其他属性
                return retrievedUser;
            }
        });
    }

    public List<User> getUsersWithInLong(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        //另外一种
//        String sqlWithIn = "SELECT * FROM users WHERE id IN ('" + String.join("','", userIds.stream().map(String::valueOf).toArray(String[]::new)) + "')";
        String sqlWithIn = "SELECT * FROM users WHERE id IN ('" + Joiner.on("','").join(userIds) + "')";
        return jdbcTemplate.query(sqlWithIn, ROW_MAPPER);
    }


    public List<User> getUserById(Long id) {
        String sqlWithInt = "SELECT * FROM users WHERE id = '" + String.valueOf(id) + "'";
        return jdbcTemplate.query(sqlWithInt, ROW_MAPPER);
    }

}