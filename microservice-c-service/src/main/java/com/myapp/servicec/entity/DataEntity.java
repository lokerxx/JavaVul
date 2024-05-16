package com.myapp.servicec.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;

    private LocalDateTime timestamp;

    // 构造器、Getter和Setter
    public DataEntity() {
    }

    public DataEntity(String data, LocalDateTime timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    // ... 省略Getter和Setter方法 ...
}