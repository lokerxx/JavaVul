package com.myapp.controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrlfController {
    @GetMapping("/crlf_injection")
    public ResponseEntity<String> example(@RequestParam("name") String name) {
        String message = "Hello, " + name;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location", "https://example.com");
        headers.set("test",name);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }
}
