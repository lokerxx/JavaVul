package com.myapp.controller;

import com.myapp.mapper.UserMapper;
import com.myapp.model.User;
import com.myapp.model.User2;
import com.myapp.model.User3;
import com.myapp.model.User4;
import com.myapp.repository.User4Repository;
import com.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;
import com.myapp.repository.User3Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    private final JdbcTemplate jdbcTemplate;
    private final UserLogic userLogic;

    private final User3Repository user3Repository;
    private final User4Repository user4Repository;

    // 构造函数注入 JdbcTemplate
    public UserController(JdbcTemplate jdbcTemplate, UserLogic userLogic, User3Repository user3Repository, User4Repository user4Repository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userLogic = userLogic;
        this.user3Repository = user3Repository;
        this.user4Repository = user4Repository;

    }


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


    // 示例：使用 Optional<String> 类型参数的方法
    @GetMapping("/users/findByOptionalUsername")
    public User findByOptionalUsername(@RequestParam Optional<String> username) {
        if (username.isPresent()) {
            String query = "SELECT * FROM users WHERE name = '" + username.get() + "'";
            // 执行查询并返回结果
            return jdbcTemplate.queryForObject(query, new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    // 设置其他属性...
                    return user;
                }
            });
        } else {
            // 用户名为空时的处理逻辑
            return null;  // 或抛出异常
        }
    }


    @PostMapping(value = "/users/get_name_object")
    public List<User> objectParam(@RequestBody User user) {
        return userLogic.getUser(user);
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

    // 这并不是一个注入，如果SAST扫描器将这个报为SQL注入漏洞，那么这是一个误报问题
    @PostMapping("/users/findByIds")
    public List<User> findUsersByIds(@RequestBody List<Long> userIds) {
        return userLogic.getUsersWithInLong(userIds);
    }

    // 这里并不存在注入，只是用来测试看SAST扫描器是否将这个误报为SQL注入漏洞
    @PostMapping("/users/getUserByUId")
    public List<User> getUserByUId(@RequestBody User user) {
        return userLogic.getUserById(user.getId());
    }

    @RequestMapping(value = "/users/jpaone")
    public List<User3> jpaOne(@RequestParam(value = "name") String name) {
        return user3Repository.findUser3ByName(name);
    }

    @RequestMapping(value = "/users/jpawithAnnotations")
    public List<User4> jpawithAnnotations(@RequestParam(value = "name") String name) {
        return user4Repository.findUser4ByName(name);
    }

}