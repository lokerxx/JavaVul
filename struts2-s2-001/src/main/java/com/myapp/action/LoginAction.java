package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport {

    private String username;
    private String password;

    @Override
    public String execute() {
        if ("admin".equals(username) && "admin123".equals(password)) {
            return SUCCESS;
        }
        addActionError("用户名或密码错误。");
        return INPUT;
    }

    @Override
    public void validate() {
        if (password == null || password.trim().isEmpty()) {
            addFieldError("password", "密码不能为空。");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
