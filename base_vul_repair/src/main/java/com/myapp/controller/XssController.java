package com.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.myapp.model.User;
import com.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;
import static org.unbescape.html.HtmlEscape.escapeHtml4;

import java.util.List;

@RestController
public class XssController {
    @Autowired
    private UserService userService;


    public static String escapeHtml(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#x27;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    @GetMapping("/xss_reflect_escapeHtml")
    public String XssTest(@RequestParam String name) {
        return "<h1>Hello, " + escapeHtml(name) + "!</h1>";
    }

    @GetMapping("/xss_reflect_htmlEscape")
    public String XssReflect_HtmlEscape(@RequestParam String name) {
        String escapedName = HtmlUtils.htmlEscape(name);
        return "<h1>Hello, " + escapedName + "!</h1>";
    }

    @GetMapping("/xss_reflect_escapeHtml4")
    public String XssReflect_EscapeHtml4(@RequestParam String name) {
        String escapedName = escapeHtml4(name);
        return "<h1>Hello, " + escapedName + "!</h1>";
    }


    @GetMapping("/xss_storage_thymeleaf")
    public ModelAndView listUsers(@RequestParam String name) {
        List<User> users = userService.findUsersByName(name);
        System.out.println(users);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("users", users);
        modelAndView.setViewName("userList");
        return modelAndView;
    }



}