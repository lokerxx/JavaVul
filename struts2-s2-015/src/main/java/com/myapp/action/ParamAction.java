package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;

public class ParamAction extends ActionSupport {

    private String message;

    @Override
    public String execute() {
        return SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
