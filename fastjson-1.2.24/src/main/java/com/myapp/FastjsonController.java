package com.myapp;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
public class FastjsonController {

    @GetMapping("/fastjson-1.2.24")
    @ResponseBody
    public String fastjson_1_2_24() {
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>JSON Form</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <form id=\"jsonForm\">\n" +
                "        <label for=\"name\">Name:</label>\n" +
                "        <input type=\"text\" id=\"name\" name=\"name\" required><br><br>\n" +
                "\n" +
                "        <label for=\"email\">Email:</label>\n" +
                "        <input type=\"email\" id=\"email\" name=\"email\" required><br><br>\n" +
                "\n" +
                "        <label for=\"age\">Age:</label>\n" +
                "        <input type=\"number\" id=\"age\" name=\"age\" required><br><br>\n" +
                "\n" +
                "        <input type=\"submit\" value=\"Submit\">\n" +
                "    </form>\n" +
                "\n" +
                "    <script>\n" +
                "        // 监听表单提交事件\n" +
                "        document.getElementById(\"jsonForm\").addEventListener(\"submit\", function(event) {\n" +
                "            event.preventDefault(); // 阻止表单默认提交行为\n" +
                "\n" +
                "            // 获取表单数据\n" +
                "            var formData = new FormData(this);\n" +
                "            var jsonData = {};\n" +
                "\n" +
                "            // 将表单数据转换为 JSON 格式\n" +
                "            formData.forEach(function(value, key) {\n" +
                "                jsonData[key] = value;\n" +
                "            });\n" +
                "\n" +
                "            // 发送 POST 请求\n" +
                "            fetch(\"/fastjson124-process\", {\n" +
                "                method: \"POST\",\n" +
                "                headers: {\n" +
                "                    \"Content-Type\": \"application/json\"\n" +
                "                },\n" +
                "                body: JSON.stringify(jsonData)\n" +
                "            })\n" +
                "            .then(response => response.text())\n" +
                "            .then(data => {\n" +
                "                // 处理响应\n" +
                "                console.log(data);\n" +
                "            });\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        return html;
    }

    @PostMapping("/fastjson1.2.24-process")
    public String fastjson1_2_24_process(@RequestBody String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        // 处理 jsonObject
        return "Processed: " + jsonObject;
    }
}
