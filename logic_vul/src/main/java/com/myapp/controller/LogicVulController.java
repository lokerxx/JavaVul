package com.myapp.controller;

import com.myapp.model.CheckoutRequest;
import com.myapp.model.CouponRedeemRequest;
import com.myapp.model.DebugBypassRequest;
import com.myapp.model.DiscountCalcRequest;
import com.myapp.model.LoginRequest;
import com.myapp.model.NegativeAmountRequest;
import com.myapp.model.OrderStateChangeRequest;
import com.myapp.model.OversellRequest;
import com.myapp.model.PaymentCallbackRequest;
import com.myapp.model.PasswordResetConfirmRequest;
import com.myapp.model.PasswordResetSendRequest;
import com.myapp.model.ApprovalRequest;
import com.myapp.model.RefundRequest;
import com.myapp.model.SmsSendRequest;
import com.myapp.model.SmsVerifyRequest;
import com.myapp.model.WalletRefundRequest;
import com.myapp.service.LogicVulService;
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

    @PostMapping("/promo/coupons/redeem/vul")
    public Map<String, Object> couponRedeemVul(@RequestBody CouponRedeemRequest request) {
        return logicVulService.couponRedeemVulnerable(request);
    }

    @PostMapping("/promo/coupons/redeem/safe")
    public Map<String, Object> couponRedeemSafe(@RequestHeader(value = "X-Logic-Token", required = false) String token,
                                                @RequestBody CouponRedeemRequest request) {
        return logicVulService.couponRedeemSafe(request, token);
    }

    @PostMapping("/payments/{orderId}/refund/vul")
    public Map<String, Object> refundVul(@PathVariable Long orderId,
                                         @RequestBody(required = false) RefundRequest request) {
        return logicVulService.refundVulnerable(orderId, request);
    }

    @PostMapping("/payments/{orderId}/refund/safe")
    public Map<String, Object> refundSafe(@PathVariable Long orderId,
                                          @RequestHeader(value = "X-Logic-Token", required = false) String token,
                                          @RequestBody(required = false) RefundRequest request) {
        return logicVulService.refundSafe(orderId, token, request);
    }

    @PostMapping("/pricing/discounts/calculate/vul")
    public Map<String, Object> discountVul(@RequestBody(required = false) DiscountCalcRequest request) {
        return logicVulService.calculateDiscountVulnerable(request);
    }

    @PostMapping("/pricing/discounts/calculate/safe")
    public Map<String, Object> discountSafe(@RequestBody(required = false) DiscountCalcRequest request) {
        return logicVulService.calculateDiscountSafe(request);
    }

    @PostMapping("/pricing/negative-amount/vul")
    public Map<String, Object> negativeAmountVul(@RequestBody(required = false) NegativeAmountRequest request) {
        return logicVulService.negativeAmountVulnerable(request);
    }

    @PostMapping("/pricing/negative-amount/safe")
    public Map<String, Object> negativeAmountSafe(@RequestBody(required = false) NegativeAmountRequest request) {
        return logicVulService.negativeAmountSafe(request);
    }

    @PostMapping("/inventory/oversell/vul")
    public Map<String, Object> oversellVul(@RequestBody(required = false) OversellRequest request) {
        return logicVulService.oversellVulnerable(request);
    }

    @PostMapping("/inventory/oversell/safe")
    public Map<String, Object> oversellSafe(@RequestBody(required = false) OversellRequest request) {
        return logicVulService.oversellSafe(request);
    }

    @PostMapping("/wallet/orders/{orderId}/refund/vul")
    public Map<String, Object> walletRefundVul(@PathVariable Long orderId,
                                               @RequestBody(required = false) WalletRefundRequest request) {
        return logicVulService.walletRefundVulnerable(orderId, request);
    }

    @PostMapping("/wallet/orders/{orderId}/refund/safe")
    public Map<String, Object> walletRefundSafe(@PathVariable Long orderId,
                                                @RequestHeader(value = "X-Logic-Token", required = false) String token,
                                                @RequestBody(required = false) WalletRefundRequest request) {
        return logicVulService.walletRefundSafe(orderId, token, request);
    }

    @PostMapping("/workflow/debug-bypass/vul")
    public Map<String, Object> debugBypassVul(@RequestBody(required = false) DebugBypassRequest request) {
        return logicVulService.debugBypassVulnerable(request);
    }

    @PostMapping("/workflow/debug-bypass/safe")
    public Map<String, Object> debugBypassSafe(@RequestHeader(value = "X-Logic-Token", required = false) String token,
                                               @RequestBody(required = false) DebugBypassRequest request) {
        return logicVulService.debugBypassSafe(request, token);
    }

    @PostMapping("/payments/callback/vul")
    public Map<String, Object> paymentCallbackVul(@RequestBody(required = false) PaymentCallbackRequest request) {
        return logicVulService.paymentCallbackVulnerable(request);
    }

    @PostMapping("/payments/callback/safe")
    public Map<String, Object> paymentCallbackSafe(@RequestBody(required = false) PaymentCallbackRequest request) {
        return logicVulService.paymentCallbackSafe(request);
    }

    @PostMapping("/auth/reset/send-vul")
    public Map<String, Object> resetSendVul(@RequestBody PasswordResetSendRequest request) {
        return logicVulService.sendResetVulnerable(request);
    }

    @PostMapping("/auth/reset/confirm-vul")
    public Map<String, Object> resetConfirmVul(@RequestBody PasswordResetConfirmRequest request) {
        return logicVulService.confirmResetVulnerable(request);
    }

    @PostMapping("/auth/reset/send-safe")
    public Map<String, Object> resetSendSafe(@RequestBody PasswordResetSendRequest request) {
        return logicVulService.sendResetSafe(request);
    }

    @PostMapping("/auth/reset/confirm-safe")
    public Map<String, Object> resetConfirmSafe(@RequestBody PasswordResetConfirmRequest request) {
        return logicVulService.confirmResetSafe(request);
    }

    @PostMapping("/workflow/approval/{taskId}/vul")
    public Map<String, Object> approvalVul(@PathVariable Long taskId,
                                           @RequestBody(required = false) ApprovalRequest request) {
        return logicVulService.approvalVulnerable(taskId, request);
    }

    @PostMapping("/workflow/approval/{taskId}/safe")
    public Map<String, Object> approvalSafe(@PathVariable Long taskId,
                                            @RequestHeader(value = "X-Logic-Token", required = false) String token,
                                            @RequestBody(required = false) ApprovalRequest request) {
        return logicVulService.approvalSafe(taskId, request, token);
    }

    @PostMapping("/workflow/orders/{orderId}/state/vul")
    public Map<String, Object> orderStateVul(@PathVariable Long orderId,
                                             @RequestBody(required = false) OrderStateChangeRequest request) {
        return logicVulService.orderStateVulnerable(orderId, request);
    }

    @PostMapping("/workflow/orders/{orderId}/state/safe")
    public Map<String, Object> orderStateSafe(@PathVariable Long orderId,
                                              @RequestHeader(value = "X-Logic-Token", required = false) String token,
                                              @RequestBody(required = false) OrderStateChangeRequest request) {
        return logicVulService.orderStateSafe(orderId, request, token);
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
