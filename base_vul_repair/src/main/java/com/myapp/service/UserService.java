package com.myapp.service;

import com.myapp.mapper.UserMapper;
import com.myapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findById(String id) {
        return userMapper.findById(id);
    }

    public List<User> findUsersByName(String name) {
        return userMapper.findUsersByName(name);
    }
}