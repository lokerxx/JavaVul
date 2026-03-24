package com.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogicVulPageController {

    @GetMapping({"/", "/logic-vul"})
    public String home() {
        return "logic-vul-home";
    }

    @GetMapping("/logic-vul/identity")
    public String identity() {
        return "logic-vul-identity";
    }

    @GetMapping("/logic-vul/horizontal")
    public String horizontal() {
        return "logic-vul-horizontal";
    }

    @GetMapping("/logic-vul/vertical")
    public String vertical() {
        return "logic-vul-vertical";
    }

    @GetMapping("/logic-vul/checkout")
    public String checkout() {
        return "logic-vul-checkout";
    }

    @GetMapping("/logic-vul/sms-code")
    public String smsCode() {
        return "logic-vul-sms-code";
    }

    @GetMapping("/logic-vul/sms-bomb")
    public String smsBomb() {
        return "logic-vul-sms-bomb";
    }

    @GetMapping("/logic-vul/coupon")
    public String coupon() {
        return "logic-vul-coupon";
    }

    @GetMapping("/logic-vul/refund")
    public String refund() {
        return "logic-vul-refund";
    }

    @GetMapping("/logic-vul/discount")
    public String discount() {
        return "logic-vul-discount";
    }

    @GetMapping("/logic-vul/negative-amount")
    public String negativeAmount() {
        return "logic-vul-negative-amount";
    }

    @GetMapping("/logic-vul/oversell")
    public String oversell() {
        return "logic-vul-oversell";
    }

    @GetMapping("/logic-vul/balance-refund")
    public String balanceRefund() {
        return "logic-vul-balance-refund";
    }

    @GetMapping("/logic-vul/debug-bypass")
    public String debugBypass() {
        return "logic-vul-debug-bypass";
    }

    @GetMapping("/logic-vul/payment-callback")
    public String paymentCallback() {
        return "logic-vul-payment-callback";
    }

    @GetMapping("/logic-vul/reset")
    public String reset() {
        return "logic-vul-reset";
    }

    @GetMapping("/logic-vul/approval")
    public String approval() {
        return "logic-vul-approval";
    }

    @GetMapping("/logic-vul/state-machine")
    public String stateMachine() {
        return "logic-vul-state-machine";
    }
}
