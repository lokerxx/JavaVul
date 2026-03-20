package com.myapp.controller;

import com.myapp.model.CheckoutRequest;
import com.myapp.model.LoginRequest;
import com.myapp.model.SmsSendRequest;
import com.myapp.model.SmsVerifyRequest;
import com.myapp.service.LogicVulService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
public class LogicVulController {
    private final LogicVulService logicVulService;

    public LogicVulController(LogicVulService logicVulService) {
        this.logicVulService = logicVulService;
    }

    @GetMapping(value = {"/", "/logic-vul"}, produces = MediaType.TEXT_HTML_VALUE)
    public String playground() {
        return logicVulService.playgroundHtml();
    }

    @GetMapping("/logic-vul/info")
    public Map<String, Object> info() {
        return logicVulService.info();
    }

    @PostMapping("/auth/login-vul")
    public Map<String, Object> loginVul(@RequestBody LoginRequest request) {
        return logicVulService.loginVulnerable(request);
    }

    @PostMapping("/auth/login-safe")
    public Map<String, Object> loginSafe(@RequestBody LoginRequest request) {
        return logicVulService.loginSafe(request);
    }

    @GetMapping("/auth/me")
    public Map<String, Object> whoAmI(@RequestHeader(value = "X-Logic-Token", required = false) String token) {
        return logicVulService.whoAmI(token);
    }

    @GetMapping("/api/personal/{profileId}/vul")
    public Map<String, Object> personalVul(@PathVariable Long profileId,
                                           @RequestParam(value = "actingUserId", required = false) Long actingUserId) {
        return logicVulService.profileVulnerable(profileId, actingUserId);
    }

    @GetMapping("/api/personal/{profileId}/safe")
    public Map<String, Object> personalSafe(@PathVariable Long profileId,
                                            @RequestHeader(value = "X-Logic-Token", required = false) String token) {
        return logicVulService.profileSafe(profileId, token);
    }

    @GetMapping("/api/admin/report/vul")
    public Map<String, Object> adminReportVul(@RequestParam(value = "actingUserId", required = false) Long actingUserId,
                                              @RequestHeader(value = "X-Client-Role", required = false) String role) {
        return logicVulService.adminReportVulnerable(actingUserId, role);
    }

    @GetMapping("/api/admin/report/safe")
    public Map<String, Object> adminReportSafe(@RequestHeader(value = "X-Logic-Token", required = false) String token) {
        return logicVulService.adminReportSafe(token);
    }

    @PostMapping("/api/orders/{orderId}/checkout/vul")
    public Map<String, Object> checkoutVul(@PathVariable Long orderId,
                                           @RequestParam(value = "actingUserId", required = false) Long actingUserId,
                                           @RequestBody(required = false) CheckoutRequest request) {
        return logicVulService.checkoutVulnerable(orderId, actingUserId, request);
    }

    @PostMapping("/api/orders/{orderId}/checkout/safe")
    public Map<String, Object> checkoutSafe(@PathVariable Long orderId,
                                            @RequestHeader(value = "X-Logic-Token", required = false) String token,
                                            @RequestBody(required = false) CheckoutRequest request) {
        return logicVulService.checkoutSafe(orderId, token, request);
    }

    @PostMapping("/sms/send-vul")
    public Map<String, Object> sendSmsVul(@RequestBody SmsSendRequest request) {
        return logicVulService.sendSmsVulnerable(request);
    }

    @PostMapping("/sms/verify-vul")
    public Map<String, Object> verifySmsVul(@RequestBody SmsVerifyRequest request) {
        return logicVulService.verifySmsVulnerable(request);
    }

    @PostMapping("/sms/send-safe")
    public Map<String, Object> sendSmsSafe(@RequestBody SmsSendRequest request) {
        return logicVulService.sendSmsSafe(request);
    }

    @PostMapping("/sms/verify-safe")
    public Map<String, Object> verifySmsSafe(@RequestBody SmsVerifyRequest request) {
        return logicVulService.verifySmsSafe(request);
    }

    @PostMapping("/sms/bomb-vul")
    public Map<String, Object> smsBombVul(@RequestParam("phoneNumber") String phoneNumber,
                                          @RequestParam(value = "batch", required = false) Integer batch) {
        return logicVulService.smsBombVulnerable(phoneNumber, batch);
    }

    @PostMapping("/sms/bomb-safe")
    public Map<String, Object> smsBombSafe(@RequestParam("phoneNumber") String phoneNumber,
                                           @RequestParam(value = "batch", required = false) Integer batch) {
        return logicVulService.smsBombSafe(phoneNumber, batch);
    }
}
