package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport {

    private Integer age;
    private String name;
    private String email;

    @Override
    public String execute() {
        addActionMessage("用户资料已提交。");
        return SUCCESS;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
