package com.myapp.service;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Service
public class DruidService {

    @Autowired
    private DruidDataSource dataSource;
    //测试IAST 是否误报SQL注入，因为开启了SQL拦截
    public String executeQueryWithParam(String id) {
        String sql = "SELECT * FROM users WHERE id = " + id; // SQL拼接
        StringBuilder result = new StringBuilder();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // 处理结果集
                result.append("User ID: ").append(rs.getInt("id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append("\n");
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return "Error executing query: " + e.getMessage();
        }
        return result.toString();
    }
}
