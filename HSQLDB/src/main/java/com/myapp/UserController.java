package com.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/hsqldb")
    public List<Map<String, Object>> hsqldb(@RequestParam String username) {
        return userService.findUsersByUsername(username);
    }

    @GetMapping("/hsqldb_repair")
    public List<Map<String, Object>> hsqldb_repair(@RequestParam String username) {
        return userService.findUsersByUsernameRepair(username);
    }


}