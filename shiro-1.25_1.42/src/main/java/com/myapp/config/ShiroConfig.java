package com.myapp.config;

import org.apache.shiro.web.servlet.IniShiroFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

@Configuration
public class ShiroConfig {

    @Bean
    public FilterRegistrationBean shiroFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new IniShiroFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("configPath", "classpath:shiro.ini");
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR);
        registration.setName("ShiroFilter");
        registration.setOrder(1);
        return registration;
    }
}
