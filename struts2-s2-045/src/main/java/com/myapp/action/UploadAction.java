package com.myapp.action;

import com.opensymphony.xwork2.ActionSupport;
import java.io.File;

public class UploadAction extends ActionSupport {

    private File upload;
    private String uploadFileName;
    private String uploadContentType;
    private String note;

    @Override
    public String execute() {
        if (uploadFileName != null) {
            addActionMessage("已接收文件：" + uploadFileName + " / 类型：" + uploadContentType);
        } else {
            addActionMessage("未收到标准上传文件，本页更适合测试异常 multipart 请求。");
        }
        return SUCCESS;
    }

    public File getUpload() { return upload; }
    public void setUpload(File upload) { this.upload = upload; }
    public String getUploadFileName() { return uploadFileName; }
    public void setUploadFileName(String uploadFileName) { this.uploadFileName = uploadFileName; }
    public String getUploadContentType() { return uploadContentType; }
    public void setUploadContentType(String uploadContentType) { this.uploadContentType = uploadContentType; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
