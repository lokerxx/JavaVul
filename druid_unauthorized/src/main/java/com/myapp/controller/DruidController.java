package com.myapp.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.myapp.service.DruidService;

@RestController
public class DruidController {
    @Autowired
    private DruidService databaseService;

    @GetMapping("/druid_sql")
    public String queryUserById(@RequestParam("id") String id) {
        return databaseService.executeQueryWithParam(id);
    }
}
