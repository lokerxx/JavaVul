package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class ResetAction extends ActionSupport implements SessionAware {

    private Map session;

    @Override
    public String execute() {
        if (session != null) {
            session.remove("user");
            session.remove("isAdmin");
        }
        return SUCCESS;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void setSession(Map session) {
        this.session = session;
    }
}
