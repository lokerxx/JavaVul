package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;

public class OrdersController extends ActionSupport {

    private String id = "3";
    private String clientName = "demo-order";
    private String note = "rest-plugin xstream demo";

    @Override
    public String execute() {
        return SUCCESS;
    }

    public String edit() {
        addActionMessage("已进入 /orders/{id}/edit 入口，可使用 application/xml 发送 XStream 负载。");
        return SUCCESS;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
