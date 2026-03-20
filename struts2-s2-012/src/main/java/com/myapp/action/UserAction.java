package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport {

    private String name;
    private String flow;

    @Override
    public String execute() {
        if ("redirect".equals(flow)) {
            return "redirect";
        }
        return SUCCESS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }
}
