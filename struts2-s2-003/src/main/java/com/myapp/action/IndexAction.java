package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class IndexAction extends ActionSupport implements SessionAware {

    private Map session;
    private String sessionUser;
    private String sessionAdmin;
    private boolean manipulated;

    @Override
    public String execute() {
        if (session == null) {
            sessionUser = "(未设置)";
            sessionAdmin = "(未设置)";
            manipulated = false;
            return SUCCESS;
        }
        Object user = session.get("user");
        Object admin = session.get("isAdmin");
        sessionUser = user == null ? "(未设置)" : String.valueOf(user);
        sessionAdmin = admin == null ? "(未设置)" : String.valueOf(admin);
        manipulated = user != null || admin != null;
        return SUCCESS;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void setSession(Map session) {
        this.session = session;
    }

    public String getSessionUser() {
        return sessionUser;
    }

    public String getSessionAdmin() {
        return sessionAdmin;
    }

    public boolean isManipulated() {
        return manipulated;
    }
}
