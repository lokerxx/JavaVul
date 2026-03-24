package com.myapp.controller;

import com.myapp.service.BruteForceLabService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class BruteForceLabController {
    private final BruteForceLabService bruteForceLabService;

    public BruteForceLabController(BruteForceLabService bruteForceLabService) {
        this.bruteForceLabService = bruteForceLabService;
    }

    @GetMapping("/auth/bruteforce-vul")
    public String vulnerableLoginPage() {
        return "bruteforce-vul";
    }

    @GetMapping("/auth/bruteforce-safe")
    public String safeLoginPage() {
        return "bruteforce-safe";
    }

    @GetMapping("/auth/bruteforce-vul/hints")
    @ResponseBody
    public Map<String, Object> vulnerableHints() {
        return bruteForceLabService.vulnerableHints();
    }

    @PostMapping("/auth/bruteforce-vul/login")
    @ResponseBody
    public Map<String, Object> vulnerableLogin(@RequestParam("username") String username,
                                               @RequestParam("password") String password) {
        return bruteForceLabService.vulnerableLogin(username, password);
    }

    @GetMapping("/auth/bruteforce-safe/captcha/new")
    @ResponseBody
    public Map<String, Object> newCaptcha() {
        return bruteForceLabService.createCaptcha();
    }

    @GetMapping(value = "/auth/bruteforce-safe/captcha/image", produces = "image/svg+xml;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> captchaImage(@RequestParam("token") String token) {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml;charset=UTF-8"))
                .body(bruteForceLabService.captchaSvg(token));
    }

    @PostMapping("/auth/bruteforce-safe/login")
    @ResponseBody
    public Map<String, Object> safeLogin(@RequestParam("username") String username,
                                         @RequestParam("password") String password,
                                         @RequestParam("captchaToken") String captchaToken,
                                         @RequestParam("captchaCode") String captchaCode) {
        return bruteForceLabService.safeLogin(username, password, captchaToken, captchaCode);
    }
}
