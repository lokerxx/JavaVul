package com.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/Hibernate_injection")
    public List<User> Hibernate_injection(@RequestParam String username) {
        return userService.findUsersByUsername(username);
    }
    @GetMapping("/Hibernate_injection_repair")
    public List<User> Hibernate_injection_repair(@RequestParam String username) {
        return userService.findUsersByUsernameRepair(username);
    }
}