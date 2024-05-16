package com.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.myapp.model.User;
import com.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class XssController {
    @Autowired
    private UserService userService;


    @GetMapping("/xss_reflect")
    public String XssTest(@RequestParam String name) {
        return "<h1>Hello, " + name + "!</h1>";
    }


    @GetMapping("/xss_storage")
    public ModelAndView listUsers(@RequestParam String name) {
        List<User> users = userService.findUsersByName(name);
        System.out.println(users);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("users", users);
        modelAndView.setViewName("userList");
        return modelAndView;
    }



    @GetMapping("/xss_dom_index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("xss_dom_index");

        return modelAndView;
    }

    @PostMapping("/xss_dom")
    public ModelAndView xss(@RequestParam("name") String name) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", name);
        modelAndView.setViewName("xss");
        return modelAndView;
    }

}