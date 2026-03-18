package com.myapp.config;

import com.myapp.support.RememberMeOracleService;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public Realm realm() {
        SimpleAccountRealm realm = new SimpleAccountRealm();
        realm.addAccount("admin", "admin123", "admin");
        realm.addAccount("user", "user123", "user");
        return realm;
    }

    @Bean
    public CookieRememberMeManager rememberMeManager(RememberMeOracleService oracleService) {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        rememberMeManager.setCookie(cookie);
        oracleService.applyDemoKey(rememberMeManager);
        return rememberMeManager;
    }

    @Bean
    public SecurityManager securityManager(Realm realm, CookieRememberMeManager rememberMeManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setRememberMeManager(rememberMeManager);
        return securityManager;
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        factoryBean.setLoginUrl("/login-page");
        factoryBean.setSuccessUrl("/profile");
        factoryBean.setUnauthorizedUrl("/login-page");

        Map<String, String> chain = new LinkedHashMap<String, String>();
        chain.put("/", "anon");
        chain.put("/login-page", "anon");
        chain.put("/shiro-1.25_1.42", "anon");
        chain.put("/login", "anon");
        chain.put("/oracle/sample", "anon");
        chain.put("/oracle/probe", "anon");
        chain.put("/oracle/mutate", "anon");
        chain.put("/oracle/sweep", "anon");
        chain.put("/oracle/info", "anon");
        chain.put("/health", "anon");
        chain.put("/logout", "logout");
        chain.put("/**", "user");
        factoryBean.setFilterChainDefinitionMap(chain);
        return factoryBean;
    }

    @Bean
    public FilterRegistrationBean shiroFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy("shiroFilter");
        filterProxy.setTargetFilterLifecycle(true);
        registration.setFilter(filterProxy);
        registration.addUrlPatterns("/*");
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR);
        registration.setName("shiroFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
