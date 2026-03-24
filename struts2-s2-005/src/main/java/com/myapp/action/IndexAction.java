package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class IndexAction extends ActionSupport {

    public static final String MARKER_PATH = "/tmp/struts2-s2-005-success";
    public static final String OUTPUT_PATH = "/tmp/struts2-s2-005-output.txt";

    private boolean markerExists;
    private boolean outputExists;
    private String outputContent;

    @Override
    public String execute() {
        File markerFile = new File(MARKER_PATH);
        File outputFile = new File(OUTPUT_PATH);
        markerExists = markerFile.exists();
        outputExists = outputFile.exists();
        outputContent = readFile(outputFile);
        return SUCCESS;
    }

    private String readFile(File file) {
        if (!file.exists()) {
            return "(暂无输出文件)";
        }
        try {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8).trim();
        } catch (IOException ex) {
            return "(读取输出失败: " + ex.getMessage() + ")";
        }
    }

    public boolean isMarkerExists() {
        return markerExists;
    }

    public boolean isOutputExists() {
        return outputExists;
    }

    public String getOutputContent() {
        return outputContent;
    }
}
