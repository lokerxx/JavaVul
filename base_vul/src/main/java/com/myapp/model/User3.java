package com.myapp.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User3 {
    @Id
    private Long id;
    private String name;
    // 其他属性及getter和setter
}