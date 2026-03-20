package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;

public class HelloWorldAction extends ActionSupport {

    @Override
    public String execute() {
        return SUCCESS;
    }
}
