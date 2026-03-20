package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;

public class IndexAction extends ActionSupport {

    private String marker;

    @Override
    public String execute() {
        return SUCCESS;
    }

    public String getMarker() { return marker; }
    public void setMarker(String marker) { this.marker = marker; }
}
