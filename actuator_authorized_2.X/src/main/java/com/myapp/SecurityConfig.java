package com.myapp;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/actuator-authorized-2x", "/error").permitAll()
                .antMatchers("/actuator", "/actuator/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .httpBasic()
                .and()
                .formLogin();
    }
}
