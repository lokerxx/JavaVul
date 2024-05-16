package com.myapp.controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;

@RestController
public class CrlfController {
    @GetMapping("/crlf_injection")
    public ResponseEntity<String> example(@RequestParam("name") String name) {
        String encodedName = UriUtils.encode(name, StandardCharsets.UTF_8);

        String message = "Hello, " + encodedName;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location", "https://example.com");
        headers.set("test",encodedName);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }
}
