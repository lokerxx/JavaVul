package com.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
//                System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase","true");
//        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase","true");
        SpringApplication.run(MyApplication.class, args);
    }
}