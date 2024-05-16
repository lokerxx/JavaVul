package com.myapp.servicee.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileOutputStream;

@RestController
public class ServiceEController {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/report")
    public void generateReport(@RequestBody String analysisData) throws Exception {
        // 生成报告
        String report = generateDataReport(analysisData);
        // 发送报告到Service F
        restTemplate.postForObject("http://service-f/audit", analysisData, Void.class);
    }
    private String generateDataReport(String analysisData) throws Exception {
        // 这里可以生成复杂的报告，比如处理分析数据，生成图表等
        generatePdfReport(analysisData);
        generateDocxReport(analysisData);
        generateXmlReport(analysisData);
        generateHtmlReport(analysisData);
        return "Report based on " + analysisData;
    }

    private void generatePdfReport(String analysisData) throws Exception {
        String fileName = generateFileName("report") + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);

        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("Report based on: " + analysisData));

        document.close();
    }
    private void generateHtmlReport(String analysisData) throws Exception {
        String fileName = generateFileName("report") + ".html";

        String htmlContent = "<html><body><h1>Report</h1><p>Report based on: " + analysisData + "</p></body></html>";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(htmlContent);
        }
    }
    private void generateXmlReport(String analysisData) throws Exception {
        String fileName = generateFileName("report") + ".xml";

        String xmlContent = "<?xml version=\"1.0\"?><report><data>" + analysisData + "</data></report>";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(xmlContent);
        }
    }
    private void generateDocxReport(String analysisData) throws Exception {
        String fileName = generateFileName("report") + ".docx";

        XWPFDocument document = new XWPFDocument();
        XWPFParagraph para = document.createParagraph();
        para.createRun().setText("Report based on: " + analysisData);

        try (FileOutputStream out = new FileOutputStream(fileName)) {
            document.write(out);
        }
    }

    private String generateFileName(String baseName) {
        String dirName = "file";
        File directory = new File(dirName);

        // 检查目录是否存在，如果不存在则创建
        if (!directory.exists()) {
            directory.mkdir(); // 创建目录
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return dirName + "/" + baseName + "-" + now.format(formatter) ;
    }


}