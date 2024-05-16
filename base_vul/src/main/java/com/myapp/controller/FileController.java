package com.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {

    @GetMapping("/file_upload")
    public ModelAndView  showUploadForm() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("upload");

        return modelAndView;
    }

    @PostMapping("/file_upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            // 将上传的文件保存到本地文件系统
            byte[] bytes = file.getBytes();
            Path path = Paths.get(file.getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/success";
    }

    @GetMapping("/file_read")
    public String readFile(@RequestParam String filePath) throws IOException {
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        return stringBuilder.toString();
    }

    @GetMapping("/file_write")
    public String writeFile(@RequestParam String fileName, @RequestParam String data) throws IOException {
        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(data);
        fileWriter.flush();
        fileWriter.close();
        return "File created successfully!";
    }

    private static final String UPLOADS_FOLDER = "uploads/";

    @GetMapping("/file_download")
    public void downloadFile(@RequestParam String fileName, HttpServletResponse response) throws IOException {
        // 确定文件路径
        String filePath = UPLOADS_FOLDER + fileName;
        System.out.println("Absolute path for UPLOADS_FOLDER: " + new File(UPLOADS_FOLDER).getAbsolutePath());
//        System.out.println(filePath);
        // 创建文件对象
        File file = new File(filePath);

        // 检查文件是否存在
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 设置响应头信息
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        // 读取文件并输出到响应流
        try (InputStream is = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            os.flush();
        }
    }


    @GetMapping("/file_delete")
    public String deleteFile(@RequestParam String fileName) throws IOException {
        // 确定文件路径
        String filePath = UPLOADS_FOLDER + fileName;

        // 创建文件对象
        File file = new File(filePath);
        File file1 = new File(UPLOADS_FOLDER +"test.txt");

        FileWriter fileWriter = new FileWriter(file1);
        fileWriter.write("测试");
        fileWriter.flush();
        fileWriter.close();


        // 检查文件是否存在
        if (!file.exists()) {

            return "文件不存在";
        }

        // 删除文件
        if (!file.delete()) {

            return "删除文件失败";
        }

        return "成功删除文件";

    }


}