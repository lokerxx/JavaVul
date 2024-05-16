package com.myapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class OpenRedirectorController {

    @RequestMapping(value = "/OpenRedirector_ModelAndView")
    public ModelAndView OpenRedirector_ModelAndView(@RequestParam(value = "url") String url) {
        return new ModelAndView("redirect:" + url);
    }

    @RequestMapping(value = "/OpenRedirector_sendRedirect")
    public void OpenRedirector_sendRedirect(@RequestParam(value = "url") String url, HttpServletResponse response) throws IOException {
        response.sendRedirect(url);
    }

    @RequestMapping(value = "/OpenRedirector_lacation")
    public void OpenRedirector_lacation(@RequestParam(value = "url") String url, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", url);
    }
}