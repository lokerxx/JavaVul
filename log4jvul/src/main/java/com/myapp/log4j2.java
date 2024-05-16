package com.myapp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
public class log4j2 {
    static final Logger logger = LogManager.getLogger(log4j2.class.getName());

    @PostMapping("/log4j2")
    @ResponseBody
    public String index(@RequestParam String name) {
        logger.info("hello ");
        logger.error(name);

        return "Welcome, " + name;
    }
}
