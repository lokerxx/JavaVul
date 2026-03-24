package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;

public class IndexAction extends ActionSupport {

    private String id;

    @Override
    public String execute() {
        return SUCCESS;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
