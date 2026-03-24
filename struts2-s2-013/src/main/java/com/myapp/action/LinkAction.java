package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;

public class LinkAction extends ActionSupport {

    private String a;

    @Override
    public String execute() {
        return SUCCESS;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }
}
