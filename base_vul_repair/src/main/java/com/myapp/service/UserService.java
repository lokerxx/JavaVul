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

    public User findById1(String id) {
        return userMapper.findById1(id);
    }

    public User findById2(long id) {
        return userMapper.findById2(id);
    }

    public List<User> findUsersByName(String name) {
        return userMapper.findUsersByName(name);
    }
}