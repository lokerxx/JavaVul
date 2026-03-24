package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;

import java.io.File;

public class ResetAction extends ActionSupport {

    @Override
    public String execute() {
        deleteIfExists(IndexAction.MARKER_PATH);
        deleteIfExists(IndexAction.OUTPUT_PATH);
        return SUCCESS;
    }

    private void deleteIfExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
