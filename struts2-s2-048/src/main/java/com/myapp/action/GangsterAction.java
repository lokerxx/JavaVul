package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;

public class GangsterAction extends ActionSupport {

    private String gangsterName;
    private String age;
    private String description;

    @Override
    public String execute() {
        return SUCCESS;
    }

    public String getGangsterName() { return gangsterName; }
    public void setGangsterName(String gangsterName) { this.gangsterName = gangsterName; }
    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
