package com.myapp.controller;

import com.myapp.mapper.UserMapper;
import com.myapp.model.User;
import com.myapp.model.User2;
import com.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    private final JdbcTemplate jdbcTemplate;
    private final UserLogic userLogic;


    // 构造函数注入 JdbcTemplate
    public UserController(JdbcTemplate jdbcTemplate, UserLogic userLogic) {
        this.jdbcTemplate = jdbcTemplate;
        this.userLogic = userLogic;
    }



    @Autowired
    private UserMapper userMapper;
    // http://127.0.0.1:8080/users/1/
    @GetMapping(value = "/users/{id}", produces = "application/json")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    // http://127.0.0.1:8080/users/ids/?ids=1,2,3
    @GetMapping("/users/ids")
    public List<User> findUsersByIds(@RequestParam String ids) {
        List<User> users = userMapper.findUsersByIds(ids);
        return users;
    }
    // http://127.0.0.1:8080/users/name?name=A
    @GetMapping("/users/name")
    public List<User> findUsersByNameLike(@RequestParam String name) {
        List<User> users = userMapper.findUsersByNameLike(name);
        return users;
    }
    // http://127.0.0.1:8080/users/sort?orderByColumn=name&orderByDirection=asc
    @GetMapping("/users/sort")
    public List<User> findUsersOrderBy(@RequestParam String orderByColumn, @RequestParam String orderByDirection) {
        List<User> users = userMapper.findUsersOrderBy(orderByColumn, orderByDirection);
        return users;
    }

    // http://127.0.0.1:8080/users/names?names=Alice&names=Bob
    @GetMapping("/users/names")
    public List<User> findUsersByNames(@RequestParam List<String> names) {
        List<User> users = userMapper.findUsersByNames(names);
        return users;
    }



    @GetMapping("/users/findByOptionalUsername")
    public User findByOptionalUsername(@RequestParam Optional<String> username) {
        return username.map(un -> {
            String query = "SELECT * FROM users WHERE name = ?";
            return jdbcTemplate.queryForObject(query, new Object[]{un}, new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    // 设置其他属性...
                    return user;
                }
            });
        }).orElse(null);  // 用户名为空时的处理逻辑
    }


    @PostMapping(value = "/users/get_name_object")
    public List<User> objectParam(@RequestBody User user) {
        return userLogic.getUser(user.getName());
    }

    //MyBatis注解方式注入
    @GetMapping("/users/by-username")
    public List<User> getUsersByUsername(@RequestParam String name) {
        return userMapper.findUsersByUsername(name);
    }

    //lombok注入
    @PostMapping("/users/lombok")
    public List<User2> findUsers(@RequestBody User2 user) {
        return userLogic.getUser2(user);
    }


}