package com.myapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RestController
public class OpenRedirectorController {

    private static final Set<String> ALLOWED_HOSTS = new HashSet<>(Arrays.asList("example.com", "myapp.com"));

    @RequestMapping(value = "/OpenRedirector_ModelAndView")
    public ModelAndView OpenRedirector_ModelAndView(@RequestParam(value = "url") String url) {
        if (isUrlAllowed(url)) {
            return new ModelAndView("redirect:" + url);
        }
        return new ModelAndView("redirect:/error"); // 重定向到错误页面或主页
    }

    @RequestMapping(value = "/OpenRedirector_sendRedirect")
    public void OpenRedirector_sendRedirect(@RequestParam(value = "url") String url, HttpServletResponse response) throws IOException {
        if (isUrlAllowed(url)) {
            response.sendRedirect(url);
        } else {
            response.sendRedirect("/error"); // 重定向到错误页面或主页
        }
    }

    @RequestMapping(value = "/OpenRedirector_lacation")
    public void OpenRedirector_lacation(@RequestParam(value = "url") String url, HttpServletResponse response) {
        if (isUrlAllowed(url)) {
            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", url);
        } else {
            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", "/error"); // 重定向到错误页面或主页
        }
    }

    private boolean isUrlAllowed(String url) {
        try {
            URL parsedUrl = new URL(url);
            return ALLOWED_HOSTS.contains(parsedUrl.getHost());
        } catch (Exception e) {
            return false;
        }
    }
}