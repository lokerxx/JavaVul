package com.myapp.model;

public class LoginRequest {
    private String username;
    private String password;
    private Long debugUserId;
    private Boolean bypassPassword;

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

    public Long getDebugUserId() {
        return debugUserId;
    }

    public void setDebugUserId(Long debugUserId) {
        this.debugUserId = debugUserId;
    }

    public Boolean getBypassPassword() {
        return bypassPassword;
    }

    public void setBypassPassword(Boolean bypassPassword) {
        this.bypassPassword = bypassPassword;
    }
}
