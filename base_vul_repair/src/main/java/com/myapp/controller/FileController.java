package com.myapp.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.myapp.util.FileFilter;


@RestController
public class FileController {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png");


    @GetMapping("/file_upload")
    public ModelAndView  showUploadForm() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("upload");

        return modelAndView;
    }

    @PostMapping("/file_upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            // 验证上传的文件是否为图片类型
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return "Invalid file type.";
//                throw new RuntimeException("Invalid file type.");
            }

            // 验证上传的文件是否为合法的图片文件
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return "Invalid image file.";
//                throw new RuntimeException("Invalid image file.");
            }

            // 限制上传文件的大小和数量
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
//                throw new RuntimeException("File size exceeds the limit.");
                return "File size exceeds the limit.";
            }

            // 将上传的文件保存到指定位置
            String uploadDir = "/uploads";
            String realPath = request.getServletContext().getRealPath(uploadDir);
            if (realPath == null) {
//                throw new RuntimeException("Failed to retrieve upload directory.");
                return "Failed to retrieve upload directory.";
            }

            Path path = Paths.get(realPath, fileName);
            Files.write(path, file.getBytes());

        } catch (IOException e) {
//            throw new RuntimeException("Failed to upload file: " + e.getMessage());
            return "Failed to upload file: " + e.getMessage();
        }

        return "redirect:/success";
    }

    private static final String UPLOADS_FOLDER = "uploads/";


    @GetMapping("/file_read")
    public String readFile(@RequestParam String filePath) throws IOException {
        File file = new File(ResourceUtils.getFile(filePath).getAbsolutePath());
        String absolutePath = file.getAbsolutePath();
        String canonicalPath = file.getCanonicalPath();
        String uploadPath = ResourceUtils.getFile(UPLOADS_FOLDER).getCanonicalPath();

        System.out.println("Absolute Path: " + absolutePath);
        System.out.println("Canonical Path: " + canonicalPath);
        System.out.println("Canonical Path: " + uploadPath);

        if (!canonicalPath.startsWith(uploadPath)) {
            return "Access denied";
        }
        if (!file.exists() || !file.isFile()) {
            return "File not found";
        }
        if (!file.canRead()) {
            return "Cannot read file";
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        return stringBuilder.toString();
    }


    @GetMapping("/file_read1")
    public String readFile1(@RequestParam String filePath) throws IOException {
        // 获取并规范化路径
        Path basePath = Paths.get(UPLOADS_FOLDER).toRealPath();
        Path resolvedPath = basePath.resolve(filePath).normalize();

        // 检查是否在指定目录下
        if (!resolvedPath.startsWith(basePath)) {
            return "Access denied";
        }

        // 检查文件是否存在、是否是文件、是否可读
        if (!Files.exists(resolvedPath) || !Files.isRegularFile(resolvedPath) || !Files.isReadable(resolvedPath)) {
            return "File not found or cannot be read";
        }

        // 读取文件内容
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(resolvedPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }

        return contentBuilder.toString();
    }



    @GetMapping("/file_read2")
    public String readFile2(@RequestParam String filePath) {
        try {
            // 首先过滤路径中的目录遍历字符
            if (!FileFilter.doFilter(filePath)) {
                return "Access denied due to invalid path";
            }

            // 验证路径是否在指定的父目录中
            if (FileFilter.isValidDirectoryPath(filePath, UPLOADS_FOLDER)) {
                Path resolvedPath = Paths.get(UPLOADS_FOLDER).resolve(filePath).normalize();

                // 检查文件是否存在、是否是文件、是否可读
                if (!Files.exists(resolvedPath) || !Files.isRegularFile(resolvedPath) || !Files.isReadable(resolvedPath)) {
                    return "File not found or cannot be read";
                }

                // 读取文件内容
                StringBuilder contentBuilder = new StringBuilder();
                try (BufferedReader reader = Files.newBufferedReader(resolvedPath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        contentBuilder.append(line).append("\n");
                    }
                }

                return contentBuilder.toString();
            } else {
                return "Access denied";
            }
        } catch (InvalidPathException | IOException e) {
            return "Invalid file path or access error";
        }
    }


    @GetMapping("/file_read3")
    public String readFile3(@RequestParam String filePath) throws IOException {
        File file = new File(ResourceUtils.getFile(filePath).getAbsolutePath());
        String absolutePath = file.getAbsolutePath();
        String canonicalPath = file.getCanonicalPath();
        String uploadPath = ResourceUtils.getFile(UPLOADS_FOLDER).getCanonicalPath();


        System.out.println("Absolute Path: " + absolutePath);
        System.out.println("Canonical Path: " + canonicalPath);
        System.out.println("Canonical Path: " + uploadPath);

        if (StringUtils.contains(filePath, "/") ) {
            return "存在目录遍历漏洞";
        }

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
        // 检查文件名是否包含目录遍历字符
        if (fileName.contains("../")) {
            return "Invalid file name";
        }

        // 检查文件名是否合法且为.log后缀
        if (!fileName.matches("^[a-zA-Z0-9_]+\\.log$")) {
            return "Invalid file name or file extension";
        }

        // 确定文件路径
        String filePath = UPLOADS_FOLDER + fileName;

        // 创建文件对象
        File file = new File(filePath);

        // 检查目录是否存在
        if (!file.getParentFile().exists()) {
            // 创建目录
            file.getParentFile().mkdirs();
        }

        // 写入文件
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data.getBytes());
        }
        System.out.println("Absolute path for UPLOADS_FOLDER: " + new File(UPLOADS_FOLDER).getAbsolutePath());

        return "File created successfully!\nAbsolute path for UPLOADS_FOLDER: "+ new File(UPLOADS_FOLDER).getAbsolutePath();
    }


    @GetMapping("/file_download")
    public void downloadFile(@RequestParam String fileName, HttpServletResponse response) throws IOException {

        // 检查文件名是否包含目录遍历字符
        if (fileName.contains("../")) {
            System.out.println("Invalid file name");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file name");

            return;
        }

        // 检查文件名是否合法且为.log后缀
        if (!fileName.matches("^[a-zA-Z0-9_]+\\.log$")) {
            System.out.println("Invalid file name or file extension");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file name or file extension");

            return;
        }

        // 确定文件路径
        String filePath = UPLOADS_FOLDER + fileName;
        // 创建文件对象
        File file = new File(filePath);

        // 检查文件是否存在
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            System.out.println("file 404");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");

            return;
        }

        // 检查文件是否在指定目录下
        if (!file.getAbsolutePath().startsWith(new File(UPLOADS_FOLDER).getAbsolutePath())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");

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
    public void deleteFile(@RequestParam String fileName, HttpServletResponse response) throws IOException {

        File file1 = new File(UPLOADS_FOLDER +"test.txt");

        FileWriter fileWriter = new FileWriter(file1);
        fileWriter.write("测试");
        fileWriter.flush();
        fileWriter.close();

        // 检查文件名是否合法
        if (!fileName.matches("^[a-zA-Z0-9_]*$")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 确定文件路径
        String filePath = UPLOADS_FOLDER + fileName;

        // 创建文件对象
        File file = new File(filePath);

        // 检查文件是否存在
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 检查文件是否在指定目录下
        if (!file.getAbsolutePath().startsWith(new File(UPLOADS_FOLDER).getAbsolutePath())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // 删除文件
        if (!file.delete()) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // 返回删除成功的响应
        response.setStatus(HttpServletResponse.SC_OK);
    }

}