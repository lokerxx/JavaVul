package com.myapp.service;

import com.myapp.model.ApprovalRequest;
import com.myapp.model.CheckoutRequest;
import com.myapp.model.CouponRedeemRequest;
import com.myapp.model.DebugBypassRequest;
import com.myapp.model.DiscountCalcRequest;
import com.myapp.model.LoginRequest;
import com.myapp.model.NegativeAmountRequest;
import com.myapp.model.OrderRecord;
import com.myapp.model.OrderStateChangeRequest;
import com.myapp.model.OversellRequest;
import com.myapp.model.PaymentCallbackRequest;
import com.myapp.model.PasswordResetConfirmRequest;
import com.myapp.model.PasswordResetSendRequest;
import com.myapp.model.PersonalProfile;
import com.myapp.model.RefundRequest;
import com.myapp.model.SmsSendRequest;
import com.myapp.model.SmsVerifyRequest;
import com.myapp.model.UserAccount;
import com.myapp.model.WalletRefundRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Service
public class LogicVulService {
    private final Map<Long, UserAccount> users = new LinkedHashMap<Long, UserAccount>();
    private final Map<Long, PersonalProfile> profiles = new LinkedHashMap<Long, PersonalProfile>();
    private final Map<Long, OrderRecord> orders = new LinkedHashMap<Long, OrderRecord>();
    private final Map<String, Long> tokens = new LinkedHashMap<String, Long>();

    private final Map<String, SmsChallenge> smsChallengesSafe = new LinkedHashMap<String, SmsChallenge>();
    private final Map<String, SmsChallenge> smsChallengesVul = new LinkedHashMap<String, SmsChallenge>();
    private final List<SmsChallenge> vulIssuedCodes = new ArrayList<SmsChallenge>();
    private final Map<String, Integer> smsSendCounterVul = new LinkedHashMap<String, Integer>();
    private final Map<String, Integer> smsSendCounterSafe = new LinkedHashMap<String, Integer>();

    private final Map<String, CouponRecord> coupons = new LinkedHashMap<String, CouponRecord>();
    private final Map<Long, RefundLedger> refundLedgers = new LinkedHashMap<Long, RefundLedger>();
    private final Map<String, ResetAccount> resetAccounts = new LinkedHashMap<String, ResetAccount>();
    private final Map<String, ResetTokenRecord> resetTokensVul = new LinkedHashMap<String, ResetTokenRecord>();
    private final Map<String, ResetTokenRecord> resetTokensSafe = new LinkedHashMap<String, ResetTokenRecord>();
    private final Map<Long, ApprovalTaskRecord> approvalTasks = new LinkedHashMap<Long, ApprovalTaskRecord>();
    private final Map<Long, WorkflowOrderRecord> workflowOrders = new LinkedHashMap<Long, WorkflowOrderRecord>();
    private final Map<String, InventoryRecord> inventoryStocks = new LinkedHashMap<String, InventoryRecord>();
    private final Map<Long, WalletAccountRecord> walletAccounts = new LinkedHashMap<Long, WalletAccountRecord>();
    private final Map<Long, WalletOrderRecord> walletOrders = new LinkedHashMap<Long, WalletOrderRecord>();
    private final Map<String, PaymentOrderRecord> paymentOrders = new LinkedHashMap<String, PaymentOrderRecord>();
    private DebugTaskRecord debugTask;

    private final Random random = new Random();

    public LogicVulService() {
        seedUsers();
        seedProfiles();
        seedOrders();
        seedCoupons();
        seedRefunds();
        seedResetAccounts();
        seedApprovalTasks();
        seedWorkflowOrders();
        seedInventoryStocks();
        seedWalletAccounts();
        seedWalletOrders();
        seedPaymentOrders();
        seedDebugTask();
    }

    private void seedUsers() {
        users.put(21L, new UserAccount(21L, "jefferey.krajcik", "dbd9dv0k19z0mqy", "USER", "胡绍齐", "hu.shaoqi@example.com"));
        users.put(23L, new UserAccount(23L, "man.hackett", "oikihbi4xb", "USER", "金天翼", "jin.tianyi@example.com"));
        users.put(27L, new UserAccount(27L, "frances.goldner", "3jwl2i3t6", "USER", "韩雨宁", "han.yuning@example.com"));
        users.put(29L, new UserAccount(29L, "yon.tremblay", "wyu3bxp4pc865s", "ADMIN", "贺修远", "he.xiuyuan@example.com"));
    }

    private void seedProfiles() {
        profiles.put(1L, new PersonalProfile(1L, 21L, "胡绍齐", "17365375549", "hu.shaoqi@example.com", "深圳市南山区科技园 8 号", "482867199511218036"));
        profiles.put(2L, new PersonalProfile(2L, 23L, "金天翼", "13078470040", "jin.tianyi@example.com", "长春市朝阳区人民大街 43716 号", "742462200007129678"));
        profiles.put(3L, new PersonalProfile(3L, 27L, "韩雨宁", "15134299958", "han.yuning@example.com", "福州市鼓楼区软件大道 3 号", "346626200606101210"));
        profiles.put(4L, new PersonalProfile(4L, 29L, "贺修远", "15933988032", "he.xiuyuan@example.com", "贵阳市观山湖区会展路 338 号", "455866198905095417"));
    }

    private void seedOrders() {
        orders.put(5001L, new OrderRecord(5001L, "0526348562", 27L, "年度会员课程", 2, 99.90, "CREATED", true));
        orders.put(5002L, new OrderRecord(5002L, "0198213310", 23L, "企业分析报告", 1, 299.00, "CREATED", false));
        orders.put(5003L, new OrderRecord(5003L, "0515374436", 21L, "限时优惠券礼包", 3, 19.90, "PAID", true));
    }

    private void seedCoupons() {
        coupons.put("WELCOME-100", new CouponRecord("WELCOME-100", 100.0, 1));
        coupons.put("VIP-50", new CouponRecord("VIP-50", 50.0, 2));
    }

    private void seedRefunds() {
        refundLedgers.put(5001L, new RefundLedger(5001L, 0.0));
        refundLedgers.put(5003L, new RefundLedger(5003L, 0.0));
    }

    private void seedResetAccounts() {
        resetAccounts.put("portal.alice", new ResetAccount("portal.alice", "Alice", "alice-reset-1"));
        resetAccounts.put("portal.bob", new ResetAccount("portal.bob", "Bob", "bob-reset-1"));
    }

    private void seedApprovalTasks() {
        approvalTasks.put(9001L, new ApprovalTaskRecord(9001L, "采购合同审批", 27L, "DRAFT", new ArrayList<String>()));
        approvalTasks.put(9002L, new ApprovalTaskRecord(9002L, "市场预算审批", 23L, "PENDING_MANAGER", new ArrayList<String>()));
    }

    private void seedWorkflowOrders() {
        workflowOrders.put(8001L, new WorkflowOrderRecord(8001L, "WF-8001", 27L, "CREATED"));
        workflowOrders.put(8002L, new WorkflowOrderRecord(8002L, "WF-8002", 23L, "PAID"));
    }

    private void seedInventoryStocks() {
        inventoryStocks.put("SKU-IPHONE-15", new InventoryRecord("SKU-IPHONE-15", "手机抢购库存", 3));
        inventoryStocks.put("SKU-VIP-COURSE", new InventoryRecord("SKU-VIP-COURSE", "会员课程名额", 1));
    }

    private void seedWalletAccounts() {
        walletAccounts.put(21L, new WalletAccountRecord(21L, 320.00));
        walletAccounts.put(23L, new WalletAccountRecord(23L, 500.00));
        walletAccounts.put(27L, new WalletAccountRecord(27L, 260.00));
    }

    private void seedWalletOrders() {
        walletOrders.put(9101L, new WalletOrderRecord(9101L, "WAL-9101", 27L, 88.00, false));
        walletOrders.put(9102L, new WalletOrderRecord(9102L, "WAL-9102", 21L, 66.00, true));
    }

    private void seedPaymentOrders() {
        paymentOrders.put("PAY-5001", new PaymentOrderRecord("PAY-5001", 99.90, "INIT", "MCH-LOGIC-001"));
        paymentOrders.put("PAY-5002", new PaymentOrderRecord("PAY-5002", 299.00, "INIT", "MCH-LOGIC-001"));
    }

    private void seedDebugTask() {
        debugTask = new DebugTaskRecord(7001L, "高风险提现审批", "PENDING_AUDIT");
    }

    public Map<String, Object> loginVulnerable(LoginRequest request) {
        if (request == null) {
            return message("请求体不能为空");
        }

        UserAccount acting;
        String reason;
        if (request.getDebugUserId() != null) {
            acting = users.get(request.getDebugUserId());
            if (acting == null) {
                return message("debugUserId 对应的用户不存在");
            }
            reason = "服务端信任了客户端传入的 debugUserId";
        } else {
            acting = findByUsername(request.getUsername());
            if (acting == null) {
                return message("用户不存在");
            }
            if (Boolean.TRUE.equals(request.getBypassPassword())) {
                reason = "服务端信任了 bypassPassword=true";
            } else if (acting.getPassword().equals(request.getPassword())) {
                reason = "用户名和密码匹配";
            } else {
                return message("密码错误；如果传 bypassPassword=true 仍可进入");
            }
        }

        return tokenResponse(acting, issueToken(acting.getId()), reason, true);
    }

    public Map<String, Object> loginSafe(LoginRequest request) {
        if (request == null) {
            return message("请求体不能为空");
        }
        UserAccount acting = findByUsername(request.getUsername());
        if (acting == null || !acting.getPassword().equals(request.getPassword())) {
            return message("用户名或密码错误");
        }
        return tokenResponse(acting, issueToken(acting.getId()), "服务端完成了正常账号口令校验", false);
    }

    public Map<String, Object> whoAmI(String token) {
        UserAccount user = requireToken(token);
        if (user == null) {
            return message("token 无效，请先调用 /auth/login-safe");
        }
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("user", sanitizeUser(user));
        data.put("token", token);
        return data;
    }

    public Map<String, Object> profileVulnerable(Long profileId, Long actingUserId) {
        PersonalProfile profile = profiles.get(profileId);
        if (profile == null) {
            return message("资料不存在");
        }
        UserAccount acting = users.get(actingUserId);

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("viewerUserId", actingUserId);
        data.put("viewerName", acting == null ? "匿名用户" : acting.getDisplayName());
        data.put("profile", profile);
        data.put("warning", "服务端只信任 actingUserId，没有校验资料归属");
        return data;
    }

    public Map<String, Object> profileSafe(Long profileId, String token) {
        PersonalProfile profile = profiles.get(profileId);
        UserAccount acting = requireToken(token);
        if (profile == null) {
            return message("资料不存在");
        }
        if (acting == null) {
            return message("token 无效");
        }
        if (!acting.getId().equals(profile.getOwnerUserId()) && !"ADMIN".equals(acting.getRole())) {
            return message("无权查看其他用户资料");
        }

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("viewer", sanitizeUser(acting));
        data.put("profile", profile);
        return data;
    }

    public Map<String, Object> adminReportVulnerable(Long actingUserId, String roleFromClient) {
        if (!"ADMIN".equalsIgnoreCase(defaultString(roleFromClient)) && !"管理员".equals(roleFromClient)) {
            return message("把 X-Client-Role 改成 ADMIN 或 管理员 就能看到报表");
        }

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("actingUserId", actingUserId);
        data.put("roleSource", roleFromClient);
        data.put("report", buildAdminReport());
        data.put("warning", "服务端信任了客户端可控的角色头");
        return data;
    }

    public Map<String, Object> adminReportSafe(String token) {
        UserAccount acting = requireToken(token);
        if (acting == null) {
            return message("token 无效");
        }
        if (!"ADMIN".equals(acting.getRole())) {
            return message("仅管理员可访问报表");
        }

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("viewer", sanitizeUser(acting));
        data.put("report", buildAdminReport());
        return data;
    }

    public Map<String, Object> checkoutVulnerable(Long orderId, Long actingUserId, CheckoutRequest request) {
        OrderRecord order = orders.get(orderId);
        if (order == null) {
            return message("订单不存在");
        }
        if (request == null) {
            request = new CheckoutRequest();
        }

        double charged = request.getClientTotal() == null ? order.serverTotal() : request.getClientTotal().doubleValue();
        if (Boolean.TRUE.equals(request.getMarkAsPaid())) {
            order.setStatus("PAID");
        }

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("actingUserId", actingUserId);
        data.put("order", order);
        data.put("serverTotal", money(order.serverTotal()));
        data.put("chargedTotal", money(charged));
        data.put("skipInventoryCheck", Boolean.TRUE.equals(request.getSkipInventoryCheck()));
        data.put("paymentReference", request.getPaymentReference());
        data.put("warning", "服务端未校验订单归属，且信任 clientTotal / markAsPaid / skipInventoryCheck");
        return data;
    }

    public Map<String, Object> checkoutSafe(Long orderId, String token, CheckoutRequest request) {
        OrderRecord order = orders.get(orderId);
        UserAccount acting = requireToken(token);
        if (order == null) {
            return message("订单不存在");
        }
        if (acting == null) {
            return message("token 无效");
        }
        if (!acting.getId().equals(order.getOwnerUserId())) {
            return message("不能操作别人的订单");
        }
        if (!order.isInventoryLocked()) {
            return message("库存尚未锁定，不能直接结算");
        }
        if (request == null || blank(request.getPaymentReference())) {
            return message("缺少支付流水号");
        }

        order.setStatus("PAID");
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("viewer", sanitizeUser(acting));
        data.put("order", order);
        data.put("chargedTotal", money(order.serverTotal()));
        data.put("paymentReference", request.getPaymentReference());
        data.put("message", "服务端使用了订单真实金额，并校验了归属与库存状态");
        return data;
    }

    public Map<String, Object> couponRedeemVulnerable(CouponRedeemRequest request) {
        if (request == null || blank(request.getCouponCode())) {
            return message("couponCode 不能为空");
        }
        CouponRecord coupon = coupons.get(request.getCouponCode());
        if (coupon == null) {
            return message("优惠券不存在");
        }
        if (coupon.remaining <= 0) {
            return message("优惠券额度已耗尽");
        }

        int requestedCount = request.getRedemptionCount() == null || request.getRedemptionCount().intValue() <= 0
                ? 1 : request.getRedemptionCount().intValue();
        int snapshotRemaining = coupon.remaining;
        coupon.remaining = Math.max(0, coupon.remaining - 1);

        double orderAmount = request.getOrderAmount() == null ? 299.0 : request.getOrderAmount().doubleValue();
        double totalDiscount = coupon.discountAmount * requestedCount;

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("actingUserId", request.getActingUserId());
        data.put("couponCode", coupon.code);
        data.put("snapshotRemaining", snapshotRemaining);
        data.put("decrementedRemaining", coupon.remaining);
        data.put("redeemedCount", requestedCount);
        data.put("orderAmount", money(orderAmount));
        data.put("finalAmount", money(Math.max(0.0, orderAmount - totalDiscount)));
        data.put("warning", "服务端信任 redemptionCount，一次性优惠券可以被并发或重复核销");
        return data;
    }

    public Map<String, Object> couponRedeemSafe(CouponRedeemRequest request, String token) {
        UserAccount acting = requireToken(token);
        if (acting == null) {
            return message("token 无效");
        }
        if (request == null || blank(request.getCouponCode())) {
            return message("couponCode 不能为空");
        }

        CouponRecord coupon = coupons.get(request.getCouponCode());
        if (coupon == null) {
            return message("优惠券不存在");
        }
        if (coupon.redeemedUserIds.contains(acting.getId())) {
            return message("当前用户已领取或使用过该优惠券");
        }
        if (coupon.remaining <= 0) {
            return message("优惠券额度已耗尽");
        }

        coupon.remaining--;
        coupon.redeemedUserIds.add(acting.getId());
        double orderAmount = request.getOrderAmount() == null ? 299.0 : request.getOrderAmount().doubleValue();

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("viewer", sanitizeUser(acting));
        data.put("couponCode", coupon.code);
        data.put("remaining", coupon.remaining);
        data.put("orderAmount", money(orderAmount));
        data.put("finalAmount", money(Math.max(0.0, orderAmount - coupon.discountAmount)));
        data.put("message", "服务端按用户维度和库存额度做了单次核销校验");
        return data;
    }

    public Map<String, Object> refundVulnerable(Long orderId, RefundRequest request) {
        OrderRecord order = orders.get(orderId);
        if (order == null) {
            return message("订单不存在");
        }
        if (request == null) {
            request = new RefundRequest();
        }

        RefundLedger ledger = getOrCreateRefundLedger(orderId);
        double amount = request.getRefundAmount() == null ? order.serverTotal() : request.getRefundAmount().doubleValue();
        ledger.totalRefunded += amount;

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("actingUserId", request.getActingUserId());
        data.put("order", order);
        data.put("refundAmount", money(amount));
        data.put("totalRefunded", money(ledger.totalRefunded));
        data.put("warning", "没有归属、支付状态和幂等校验，同一退款请求可被重复重放");
        return data;
    }

    public Map<String, Object> refundSafe(Long orderId, String token, RefundRequest request) {
        OrderRecord order = orders.get(orderId);
        UserAccount acting = requireToken(token);
        if (order == null) {
            return message("订单不存在");
        }
        if (acting == null) {
            return message("token 无效");
        }
        if (!acting.getId().equals(order.getOwnerUserId()) && !"ADMIN".equals(acting.getRole())) {
            return message("当前用户不能退款该订单");
        }
        if (!"PAID".equals(order.getStatus())) {
            return message("仅已支付订单可退款");
        }
        if (request == null || blank(request.getIdempotencyKey())) {
            return message("idempotencyKey 不能为空");
        }

        RefundLedger ledger = getOrCreateRefundLedger(orderId);
        if (ledger.usedKeys.contains(request.getIdempotencyKey())) {
            return message("命中幂等键，已阻止重复退款");
        }

        double amount = request.getRefundAmount() == null ? order.serverTotal() : request.getRefundAmount().doubleValue();
        double remain = order.serverTotal() - ledger.totalRefunded;
        if (amount <= 0) {
            return message("退款金额必须大于 0");
        }
        if (amount > remain) {
            return message("退款金额超过剩余可退额度");
        }

        ledger.totalRefunded += amount;
        ledger.usedKeys.add(request.getIdempotencyKey());

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("viewer", sanitizeUser(acting));
        data.put("order", order);
        data.put("refundAmount", money(amount));
        data.put("totalRefunded", money(ledger.totalRefunded));
        data.put("remainingRefundable", money(Math.max(0.0, order.serverTotal() - ledger.totalRefunded)));
        data.put("message", "服务端完成了归属、支付状态和幂等校验");
        return data;
    }

    public Map<String, Object> calculateDiscountVulnerable(DiscountCalcRequest request) {
        if (request == null) {
            request = new DiscountCalcRequest();
        }

        double base = request.getBaseAmount() == null ? 299.0 : request.getBaseAmount().doubleValue();
        double couponAmount = request.getCouponAmount() == null ? 80.0 : request.getCouponAmount().doubleValue();
        double vipRate = request.getVipRate() == null ? 0.15 : request.getVipRate().doubleValue();
        double flashSaleRate = request.getFlashSaleRate() == null ? 0.20 : request.getFlashSaleRate().doubleValue();
        double pointsAmount = request.getPointsAmount() == null ? 50.0 : request.getPointsAmount().doubleValue();

        double finalAmount = (base - couponAmount - pointsAmount) * (1 - vipRate) * (1 - flashSaleRate);
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("baseAmount", money(base));
        data.put("couponAmount", money(couponAmount));
        data.put("vipRate", vipRate);
        data.put("flashSaleRate", flashSaleRate);
        data.put("pointsAmount", money(pointsAmount));
        data.put("finalAmount", money(finalAmount));
        data.put("warning", "服务端允许优惠券、会员折扣、闪购和积分无限叠加，甚至可能出现负价");
        return data;
    }

    public Map<String, Object> calculateDiscountSafe(DiscountCalcRequest request) {
        if (request == null) {
            request = new DiscountCalcRequest();
        }

        double base = request.getBaseAmount() == null ? 299.0 : request.getBaseAmount().doubleValue();
        double couponAmount = clampMoney(request.getCouponAmount() == null ? 80.0 : request.getCouponAmount().doubleValue());
        double vipRate = clampRate(request.getVipRate() == null ? 0.15 : request.getVipRate().doubleValue());
        double flashSaleRate = clampRate(request.getFlashSaleRate() == null ? 0.20 : request.getFlashSaleRate().doubleValue());
        double pointsAmount = clampMoney(request.getPointsAmount() == null ? 50.0 : request.getPointsAmount().doubleValue());

        double bestRate = Math.max(vipRate, flashSaleRate);
        double bestAmount = Math.max(couponAmount, pointsAmount);
        double finalAmount = Math.max(0.01, base * (1 - bestRate) - bestAmount);

        List<String> appliedRules = new ArrayList<String>();
        appliedRules.add(bestRate == vipRate ? "会员折扣" : "闪购折扣");
        appliedRules.add(bestAmount == couponAmount ? "优惠券" : "积分抵扣");

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("baseAmount", money(base));
        data.put("bestRate", bestRate);
        data.put("bestAmount", money(bestAmount));
        data.put("appliedRules", appliedRules);
        data.put("finalAmount", money(finalAmount));
        data.put("message", "服务端限制折扣组合，只允许一类比例折扣与一类金额抵扣生效");
        return data;
    }

    public Map<String, Object> negativeAmountVulnerable(NegativeAmountRequest request) {
        if (request == null) {
            request = new NegativeAmountRequest();
        }
        int quantity = request.getQuantity() == null ? 1 : request.getQuantity().intValue();
        double unitPrice = request.getUnitPrice() == null ? 199.0 : request.getUnitPrice().doubleValue();
        double couponAmount = request.getCouponAmount() == null ? 20.0 : request.getCouponAmount().doubleValue();
        double originalAmount = quantity * unitPrice;
        double payAmount = originalAmount - couponAmount;

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("actingUserId", request.getActingUserId());
        data.put("quantity", quantity);
        data.put("unitPrice", money(unitPrice));
        data.put("couponAmount", money(couponAmount));
        data.put("originalAmount", money(originalAmount));
        data.put("payAmount", money(payAmount));
        data.put("warning", "服务端没有拦截负数数量、负数金额和异常优惠金额，可能出现倒贴或套利");
        return data;
    }

    public Map<String, Object> negativeAmountSafe(NegativeAmountRequest request) {
        if (request == null) {
            return message("请求体不能为空");
        }
        int quantity = request.getQuantity() == null ? 1 : request.getQuantity().intValue();
        double unitPrice = request.getUnitPrice() == null ? 199.0 : request.getUnitPrice().doubleValue();
        double couponAmount = request.getCouponAmount() == null ? 20.0 : request.getCouponAmount().doubleValue();
        if (quantity <= 0) {
            return message("商品数量必须大于 0");
        }
        if (unitPrice <= 0) {
            return message("商品单价必须大于 0");
        }
        if (couponAmount < 0) {
            return message("优惠金额不能为负数");
        }
        double originalAmount = quantity * unitPrice;
        if (couponAmount > originalAmount) {
            return message("优惠金额不能超过原始订单金额");
        }

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("quantity", quantity);
        data.put("unitPrice", money(unitPrice));
        data.put("couponAmount", money(couponAmount));
        data.put("payAmount", money(originalAmount - couponAmount));
        data.put("message", "服务端已对数量、单价和优惠金额范围进行校验");
        return data;
    }

    public Map<String, Object> oversellVulnerable(OversellRequest request) {
        if (request == null || blank(request.getSkuCode())) {
            return message("skuCode 不能为空");
        }
        InventoryRecord inventory = inventoryStocks.get(request.getSkuCode());
        if (inventory == null) {
            return message("库存商品不存在");
        }
        int quantity = request.getPurchaseQuantity() == null ? 1 : request.getPurchaseQuantity().intValue();
        int parallel = request.getParallelRequests() == null ? 3 : request.getParallelRequests().intValue();
        int before = inventory.stock;
        if (before <= 0) {
            return message("库存不足");
        }
        inventory.stock = inventory.stock - (quantity * parallel);

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("skuCode", inventory.skuCode);
        data.put("productName", inventory.productName);
        data.put("beforeStock", before);
        data.put("purchaseQuantity", quantity);
        data.put("parallelRequests", parallel);
        data.put("afterStock", inventory.stock);
        data.put("warning", "服务端先校验后扣减，且信任并发次数，容易出现库存超卖");
        return data;
    }

    public Map<String, Object> oversellSafe(OversellRequest request) {
        if (request == null || blank(request.getSkuCode())) {
            return message("skuCode 不能为空");
        }
        InventoryRecord inventory = inventoryStocks.get(request.getSkuCode());
        if (inventory == null) {
            return message("库存商品不存在");
        }
        int quantity = request.getPurchaseQuantity() == null ? 1 : request.getPurchaseQuantity().intValue();
        if (quantity <= 0) {
            return message("购买数量必须大于 0");
        }
        if (quantity > inventory.stock) {
            return message("库存不足，无法完成扣减");
        }
        int before = inventory.stock;
        inventory.stock = inventory.stock - quantity;

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("skuCode", inventory.skuCode);
        data.put("productName", inventory.productName);
        data.put("beforeStock", before);
        data.put("purchaseQuantity", quantity);
        data.put("afterStock", inventory.stock);
        data.put("message", "服务端按实际购买数量进行单次扣减并校验剩余库存");
        return data;
    }

    public Map<String, Object> walletRefundVulnerable(Long orderId, WalletRefundRequest request) {
        WalletOrderRecord order = walletOrders.get(orderId);
        if (order == null) {
            return message("钱包订单不存在");
        }
        if (request == null) {
            request = new WalletRefundRequest();
        }
        WalletAccountRecord wallet = walletAccounts.get(order.ownerUserId);
        double amount = request.getRefundAmount() == null ? order.amount : request.getRefundAmount().doubleValue();
        double before = wallet.balance;
        wallet.balance += amount;
        order.refunded = true;

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("actingUserId", request.getActingUserId());
        data.put("order", sanitizeWalletOrder(order));
        data.put("walletUserId", wallet.userId);
        data.put("beforeBalance", money(before));
        data.put("refundAmount", money(amount));
        data.put("afterBalance", money(wallet.balance));
        data.put("warning", "服务端没有校验退款是否已完成，也没有幂等保护，余额可以被重复退回");
        return data;
    }

    public Map<String, Object> walletRefundSafe(Long orderId, String token, WalletRefundRequest request) {
        WalletOrderRecord order = walletOrders.get(orderId);
        UserAccount acting = requireToken(token);
        if (order == null) {
            return message("钱包订单不存在");
        }
        if (acting == null) {
            return message("token 无效");
        }
        if (!acting.getId().equals(order.ownerUserId)) {
            return message("不能操作其他用户的钱包订单");
        }
        if (request == null || blank(request.getIdempotencyKey())) {
            return message("idempotencyKey 不能为空");
        }
        if (order.usedRefundKeys.contains(request.getIdempotencyKey())) {
            return message("命中幂等键，已阻止重复退款");
        }
        if (order.refunded) {
            return message("该钱包订单已完成退款");
        }

        double amount = request.getRefundAmount() == null ? order.amount : request.getRefundAmount().doubleValue();
        if (amount <= 0 || amount > order.amount) {
            return message("退款金额不合法");
        }
        WalletAccountRecord wallet = walletAccounts.get(order.ownerUserId);
        double before = wallet.balance;
        wallet.balance += amount;
        order.refunded = true;
        order.usedRefundKeys.add(request.getIdempotencyKey());

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("viewer", sanitizeUser(acting));
        data.put("order", sanitizeWalletOrder(order));
        data.put("beforeBalance", money(before));
        data.put("refundAmount", money(amount));
        data.put("afterBalance", money(wallet.balance));
        data.put("message", "服务端校验了订单归属、退款状态和幂等键");
        return data;
    }

    public Map<String, Object> debugBypassVulnerable(DebugBypassRequest request) {
        if (request == null) {
            request = new DebugBypassRequest();
        }
        String before = debugTask.status;
        if (Boolean.TRUE.equals(request.getDebugMode()) || Boolean.TRUE.equals(request.getSkipAudit())) {
            debugTask.status = "APPROVED";
        }

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("actingUserId", request.getActingUserId());
        data.put("beforeStatus", before);
        data.put("afterStatus", debugTask.status);
        data.put("reason", request.getReason());
        data.put("warning", "服务端信任 debugMode 或 skipAudit 调试参数，导致审批被直接跳过");
        return data;
    }

    public Map<String, Object> debugBypassSafe(DebugBypassRequest request, String token) {
        UserAccount acting = requireToken(token);
        if (acting == null) {
            return message("token 无效");
        }
        if (!"ADMIN".equals(acting.getRole())) {
            return message("仅管理员可以执行审批操作");
        }
        if (request == null || blank(request.getReason())) {
            return message("reason 不能为空");
        }
        String before = debugTask.status;
        if (!"PENDING_AUDIT".equals(before)) {
            return message("当前任务状态不允许重复审批");
        }
        debugTask.status = "APPROVED";

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("viewer", sanitizeUser(acting));
        data.put("beforeStatus", before);
        data.put("afterStatus", debugTask.status);
        data.put("message", "安全版忽略调试参数，只允许管理员基于正式审批动作通过任务");
        return data;
    }

    public Map<String, Object> paymentCallbackVulnerable(PaymentCallbackRequest request) {
        if (request == null || blank(request.getOrderNumber())) {
            return message("orderNumber 不能为空");
        }
        PaymentOrderRecord order = paymentOrders.get(request.getOrderNumber());
        if (order == null) {
            return message("支付订单不存在");
        }
        if ("SUCCESS".equalsIgnoreCase(defaultString(request.getStatus()))) {
            order.status = "PAID";
            order.lastCallbackAmount = request.getAmount() == null ? 0.0 : request.getAmount().doubleValue();
        }

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("order", sanitizePaymentOrder(order));
        data.put("callbackStatus", request.getStatus());
        data.put("callbackAmount", request.getAmount());
        data.put("warning", "服务端只看状态字段就把订单标记为已支付，没有校验签名、金额和商户号");
        return data;
    }

    public Map<String, Object> paymentCallbackSafe(PaymentCallbackRequest request) {
        if (request == null || blank(request.getOrderNumber())) {
            return message("orderNumber 不能为空");
        }
        PaymentOrderRecord order = paymentOrders.get(request.getOrderNumber());
        if (order == null) {
            return message("支付订单不存在");
        }
        if (!"SUCCESS".equalsIgnoreCase(defaultString(request.getStatus()))) {
            return message("仅处理成功支付回调");
        }
        if (request.getAmount() == null || Math.abs(request.getAmount().doubleValue() - order.amount) > 0.001) {
            return message("回调金额与订单金额不一致");
        }
        if (!order.merchantId.equals(request.getMerchantId())) {
            return message("商户号不匹配");
        }
        String expectedSign = paymentSign(order.orderNumber, order.amount, order.merchantId);
        if (!expectedSign.equals(defaultString(request.getSign()))) {
            return message("支付回调签名校验失败");
        }
        order.status = "PAID";
        order.lastCallbackAmount = request.getAmount().doubleValue();

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("order", sanitizePaymentOrder(order));
        data.put("message", "安全版已校验金额、商户号与签名后再更新订单状态");
        data.put("expectedDemoSign", expectedSign);
        return data;
    }

    public Map<String, Object> sendResetVulnerable(PasswordResetSendRequest request) {
        if (request == null || blank(request.getUsername())) {
            return message("username 不能为空");
        }
        ResetAccount account = resetAccounts.get(request.getUsername());
        if (account == null) {
            return message("重置账号不存在");
        }

        String token = "reset-" + account.username;
        resetTokensVul.put(token, new ResetTokenRecord(token, account.username, System.currentTimeMillis() + 24 * 60 * 60 * 1000L));

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("username", account.username);
        data.put("resetToken", token);
        data.put("warning", "重置令牌可预测，可被重复使用，且提交时还能改 targetUsername");
        return data;
    }

    public Map<String, Object> confirmResetVulnerable(PasswordResetConfirmRequest request) {
        if (request == null || blank(request.getToken()) || blank(request.getNewPassword())) {
            return message("token 和 newPassword 不能为空");
        }
        ResetTokenRecord tokenRecord = resetTokensVul.get(request.getToken());
        if (tokenRecord == null) {
            return message("重置令牌不存在");
        }

        String targetUsername = blank(request.getTargetUsername()) ? tokenRecord.username : request.getTargetUsername();
        ResetAccount account = resetAccounts.get(targetUsername);
        if (account == null) {
            return message("目标账号不存在");
        }
        account.password = request.getNewPassword();

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("tokenOwner", tokenRecord.username);
        data.put("targetUsername", targetUsername);
        data.put("newPassword", account.password);
        data.put("warning", "令牌未绑定目标账号，也没有单次使用和过期校验");
        return data;
    }

    public Map<String, Object> sendResetSafe(PasswordResetSendRequest request) {
        if (request == null || blank(request.getUsername())) {
            return message("username 不能为空");
        }
        ResetAccount account = resetAccounts.get(request.getUsername());
        if (account == null) {
            return message("重置账号不存在");
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        long expiresAt = System.currentTimeMillis() + 2 * 60 * 1000L;
        resetTokensSafe.put(token, new ResetTokenRecord(token, account.username, expiresAt));

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("username", account.username);
        data.put("resetToken", token);
        data.put("expiresInSeconds", 120);
        data.put("message", "安全版令牌随机生成、带有效期且只绑定单一账号");
        return data;
    }

    public Map<String, Object> confirmResetSafe(PasswordResetConfirmRequest request) {
        if (request == null || blank(request.getToken()) || blank(request.getNewPassword())) {
            return message("token 和 newPassword 不能为空");
        }
        ResetTokenRecord tokenRecord = resetTokensSafe.get(request.getToken());
        if (tokenRecord == null) {
            return message("重置令牌不存在");
        }
        if (tokenRecord.used) {
            return message("重置令牌已被使用");
        }
        if (tokenRecord.expiresAt < System.currentTimeMillis()) {
            return message("重置令牌已过期");
        }
        if (!blank(request.getTargetUsername()) && !tokenRecord.username.equals(request.getTargetUsername())) {
            return message("重置令牌与目标账号不匹配");
        }
        if (request.getNewPassword().trim().length() < 8) {
            return message("新密码长度至少为 8 位");
        }

        ResetAccount account = resetAccounts.get(tokenRecord.username);
        if (account == null) {
            return message("账号不存在");
        }
        account.password = request.getNewPassword().trim();
        tokenRecord.used = true;

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("username", account.username);
        data.put("newPassword", account.password);
        data.put("message", "安全版令牌已校验绑定关系、有效期和单次使用状态");
        return data;
    }

    public Map<String, Object> approvalVulnerable(Long taskId, ApprovalRequest request) {
        ApprovalTaskRecord task = approvalTasks.get(taskId);
        if (task == null) {
            return message("审批任务不存在");
        }
        if (request == null) {
            request = new ApprovalRequest();
        }

        String targetStatus = blank(request.getTargetStatus()) ? "APPROVED" : request.getTargetStatus().trim().toUpperCase(Locale.ROOT);
        task.status = targetStatus;
        task.history.add("漏洞版：用户 " + request.getActingUserId() + " 直接把状态改成 " + targetStatus);

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("task", sanitizeApproval(task));
        data.put("warning", "服务端信任 targetStatus，审批流可被直接跳步");
        return data;
    }

    public Map<String, Object> approvalSafe(Long taskId, ApprovalRequest request, String token) {
        ApprovalTaskRecord task = approvalTasks.get(taskId);
        UserAccount acting = requireToken(token);
        if (task == null) {
            return message("审批任务不存在");
        }
        if (acting == null) {
            return message("token 无效");
        }
        if (request == null || blank(request.getAction())) {
            return message("action 不能为空");
        }

        String action = request.getAction().trim().toUpperCase(Locale.ROOT);
        if ("SUBMIT".equals(action) && "DRAFT".equals(task.status) && acting.getId().equals(task.creatorUserId)) {
            task.status = "PENDING_MANAGER";
            task.history.add("安全版：创建人提交审批");
        } else if ("APPROVE".equals(action) && "PENDING_MANAGER".equals(task.status) && "ADMIN".equals(acting.getRole())) {
            task.status = "APPROVED";
            task.history.add("安全版：管理员审批通过");
        } else if ("REJECT".equals(action) && "PENDING_MANAGER".equals(task.status) && "ADMIN".equals(acting.getRole())) {
            task.status = "REJECTED";
            task.history.add("安全版：管理员驳回审批");
        } else {
            return message("当前状态和角色不允许执行该审批动作");
        }

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("viewer", sanitizeUser(acting));
        data.put("task", sanitizeApproval(task));
        data.put("message", "审批流按固定动作推进，不能直接指定目标状态");
        return data;
    }

    public Map<String, Object> orderStateVulnerable(Long orderId, OrderStateChangeRequest request) {
        WorkflowOrderRecord order = workflowOrders.get(orderId);
        if (order == null) {
            return message("工作流订单不存在");
        }
        if (request == null) {
            request = new OrderStateChangeRequest();
        }

        String targetStatus = blank(request.getTargetStatus()) ? "COMPLETED" : request.getTargetStatus().trim().toUpperCase(Locale.ROOT);
        String previous = order.status;
        order.status = targetStatus;

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("actingUserId", request.getActingUserId());
        data.put("fromStatus", previous);
        data.put("toStatus", targetStatus);
        data.put("order", sanitizeWorkflowOrder(order));
        data.put("warning", "服务端直接信任 targetStatus，订单可以越过中间状态");
        return data;
    }

    public Map<String, Object> orderStateSafe(Long orderId, OrderStateChangeRequest request, String token) {
        WorkflowOrderRecord order = workflowOrders.get(orderId);
        UserAccount acting = requireToken(token);
        if (order == null) {
            return message("工作流订单不存在");
        }
        if (acting == null) {
            return message("token 无效");
        }
        if (request == null || blank(request.getAction())) {
            return message("action 不能为空");
        }

        String action = request.getAction().trim().toUpperCase(Locale.ROOT);
        if ("PAY".equals(action) && "CREATED".equals(order.status) && acting.getId().equals(order.ownerUserId)) {
            order.status = "PAID";
        } else if ("SHIP".equals(action) && "PAID".equals(order.status) && "ADMIN".equals(acting.getRole())) {
            order.status = "SHIPPED";
        } else if ("COMPLETE".equals(action) && "SHIPPED".equals(order.status) && acting.getId().equals(order.ownerUserId)) {
            order.status = "COMPLETED";
        } else {
            return message("当前状态与角色不允许执行该流转");
        }

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("viewer", sanitizeUser(acting));
        data.put("order", sanitizeWorkflowOrder(order));
        data.put("message", "订单必须按 CREATED -> PAID -> SHIPPED -> COMPLETED 顺序流转");
        return data;
    }

    public Map<String, Object> info() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("users", sanitizeUsers(users.values()));
        data.put("profiles", new ArrayList<PersonalProfile>(profiles.values()));
        data.put("orders", new ArrayList<OrderRecord>(orders.values()));
        data.put("smsUsers", smsProfiles());
        data.put("couponLabs", couponSnapshot());
        data.put("refundLabs", refundSnapshot());
        data.put("resetAccounts", resetAccountSnapshot());
        data.put("approvalTasks", approvalSnapshot());
        data.put("workflowOrders", workflowOrderSnapshot());
        data.put("inventoryLabs", inventorySnapshot());
        data.put("walletLabs", walletSnapshot());
        data.put("debugLab", debugSnapshot());
        data.put("paymentLabs", paymentSnapshot());
        data.put("scenarios", scenarioList());
        return data;
    }

    public Map<String, Object> sendSmsVulnerable(SmsSendRequest request) {
        if (request == null || blank(request.getPhoneNumber())) {
            return message("phoneNumber 不能为空");
        }

        String code = newCode();
        SmsChallenge challenge = new SmsChallenge(request.getPhoneNumber(), code, false, 0);
        smsChallengesVul.put(request.getPhoneNumber(), challenge);
        vulIssuedCodes.add(challenge);
        int sentCount = increaseCounter(smsSendCounterVul, request.getPhoneNumber());

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("phoneNumber", request.getPhoneNumber());
        data.put("smsCode", code);
        data.put("sendCount", sentCount);
        data.put("warning", "验证码直接回显给前端，且旧验证码仍然有效");
        return data;
    }

    public Map<String, Object> verifySmsVulnerable(SmsVerifyRequest request) {
        if (request == null || blank(request.getPhoneNumber()) || blank(request.getSmsCode())) {
            return message("phoneNumber 和 smsCode 不能为空");
        }

        SmsChallenge matched = null;
        for (SmsChallenge challenge : vulIssuedCodes) {
            if (request.getSmsCode().trim().equals(challenge.code)) {
                matched = challenge;
                break;
            }
        }
        if (matched == null) {
            return message("验证码错误");
        }

        UserAccount account = findByPhone(request.getPhoneNumber());
        if (account == null) {
            return message("手机号未找到对应用户");
        }

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("targetPhone", request.getPhoneNumber());
        data.put("matchedCodeFromPhone", matched.phoneNumber);
        data.put("user", sanitizeUser(account));
        data.put("warning", "验证码没有与手机号绑定，且成功后仍可重复使用");
        return data;
    }

    public Map<String, Object> sendSmsSafe(SmsSendRequest request) {
        if (request == null || blank(request.getPhoneNumber())) {
            return message("phoneNumber 不能为空");
        }

        int currentCount = smsSendCounterSafe.containsKey(request.getPhoneNumber())
                ? smsSendCounterSafe.get(request.getPhoneNumber()) : 0;
        if (currentCount >= 3) {
            return message("发送过于频繁，安全版已触发限流");
        }

        String code = newCode();
        SmsChallenge challenge = new SmsChallenge(request.getPhoneNumber(), code, false, 5);
        smsChallengesSafe.put(request.getPhoneNumber(), challenge);
        int sentCount = increaseCounter(smsSendCounterSafe, request.getPhoneNumber());

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("phoneNumber", request.getPhoneNumber());
        data.put("sendCount", sentCount);
        data.put("demoCode", code);
        data.put("message", "安全版不会回显正式验证码；当前字段仅用于靶场演示");
        return data;
    }

    public Map<String, Object> verifySmsSafe(SmsVerifyRequest request) {
        if (request == null || blank(request.getPhoneNumber()) || blank(request.getSmsCode())) {
            return message("phoneNumber 和 smsCode 不能为空");
        }

        SmsChallenge challenge = smsChallengesSafe.get(request.getPhoneNumber());
        if (challenge == null) {
            return message("请先发送验证码");
        }
        if (challenge.used) {
            return message("验证码已失效，请重新获取");
        }
        if (!request.getSmsCode().trim().equals(challenge.code)) {
            challenge.remainingAttempts--;
            if (challenge.remainingAttempts <= 0) {
                smsChallengesSafe.remove(request.getPhoneNumber());
                return message("验证码已失效，请重新获取");
            }
            return message("验证码错误，剩余尝试次数：" + challenge.remainingAttempts);
        }

        UserAccount account = findByPhone(request.getPhoneNumber());
        if (account == null) {
            return message("手机号未找到对应用户");
        }

        challenge.used = true;
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("phoneNumber", request.getPhoneNumber());
        data.put("user", sanitizeUser(account));
        data.put("message", "安全版验证码与手机号绑定，验证成功后立即作废");
        return data;
    }

    public Map<String, Object> smsBombVulnerable(String phoneNumber, Integer batch) {
        if (blank(phoneNumber)) {
            return message("phoneNumber 不能为空");
        }

        int times = batch == null || batch.intValue() <= 0 ? 5 : batch.intValue();
        List<String> issuedCodes = new ArrayList<String>();
        for (int i = 0; i < times; i++) {
            String code = newCode();
            SmsChallenge challenge = new SmsChallenge(phoneNumber, code, false, 0);
            smsChallengesVul.put(phoneNumber, challenge);
            vulIssuedCodes.add(challenge);
            issuedCodes.add(code);
            increaseCounter(smsSendCounterVul, phoneNumber);
        }

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "vulnerable");
        data.put("phoneNumber", phoneNumber);
        data.put("batch", times);
        data.put("sendCount", smsSendCounterVul.get(phoneNumber));
        data.put("issuedCodes", issuedCodes);
        data.put("warning", "漏洞版没有图形验证码、冷却时间和发送频控，容易被短信轰炸");
        return data;
    }

    public Map<String, Object> smsBombSafe(String phoneNumber, Integer batch) {
        if (blank(phoneNumber)) {
            return message("phoneNumber 不能为空");
        }

        int requested = batch == null || batch.intValue() <= 0 ? 5 : batch.intValue();
        int allowed = 3;
        int current = smsSendCounterSafe.containsKey(phoneNumber) ? smsSendCounterSafe.get(phoneNumber) : 0;

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("mode", "safe");
        data.put("phoneNumber", phoneNumber);
        data.put("requestedBatch", requested);
        data.put("alreadySent", current);
        if (current >= allowed) {
            data.put("actualSent", 0);
            data.put("remainingQuota", 0);
            data.put("message", "安全版命中频率限制，本轮请求被拦截");
            return data;
        }

        int actual = Math.min(requested, allowed - current);
        for (int i = 0; i < actual; i++) {
            increaseCounter(smsSendCounterSafe, phoneNumber);
        }
        data.put("actualSent", actual);
        data.put("remainingQuota", allowed - smsSendCounterSafe.get(phoneNumber));
        data.put("message", "安全版对单手机号做了发送频控");
        return data;
    }

    private UserAccount findByUsername(String username) {
        if (username == null) {
            return null;
        }
        for (UserAccount user : users.values()) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    private String issueToken(Long userId) {
        String token = "logic-" + UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, userId);
        return token;
    }

    private UserAccount requireToken(String token) {
        if (token == null) {
            return null;
        }
        Long userId = tokens.get(token);
        if (userId == null) {
            return null;
        }
        return users.get(userId);
    }

    private Map<String, Object> sanitizeUser(UserAccount user) {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("displayName", user.getDisplayName());
        data.put("role", user.getRole());
        data.put("email", user.getEmail());
        return data;
    }

    private List<Map<String, Object>> sanitizeUsers(Collection<UserAccount> accounts) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (UserAccount account : accounts) {
            list.add(sanitizeUser(account));
        }
        return list;
    }

    private Map<String, Object> tokenResponse(UserAccount user, String token, String reason, boolean vulnerable) {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("token", token);
        data.put("user", sanitizeUser(user));
        data.put("reason", reason);
        data.put("mode", vulnerable ? "vulnerable" : "safe");
        return data;
    }

    private Map<String, Object> buildAdminReport() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("registeredUsers", users.size());
        data.put("paidOrders", countOrdersByStatus("PAID"));
        data.put("createdOrders", countOrdersByStatus("CREATED"));
        data.put("highValueCustomers", new ArrayList<Map<String, Object>>(sanitizeUsers(users.values()).subList(0, 2)));
        return data;
    }

    private int countOrdersByStatus(String status) {
        int count = 0;
        for (OrderRecord order : orders.values()) {
            if (status.equals(order.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private RefundLedger getOrCreateRefundLedger(Long orderId) {
        RefundLedger ledger = refundLedgers.get(orderId);
        if (ledger == null) {
            ledger = new RefundLedger(orderId, 0.0);
            refundLedgers.put(orderId, ledger);
        }
        return ledger;
    }

    private List<Map<String, Object>> couponSnapshot() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (CouponRecord coupon : coupons.values()) {
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("couponCode", coupon.code);
            item.put("discountAmount", coupon.discountAmount);
            item.put("remaining", coupon.remaining);
            item.put("redeemedUserIds", new ArrayList<Long>(coupon.redeemedUserIds));
            list.add(item);
        }
        return list;
    }

    private List<Map<String, Object>> refundSnapshot() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (RefundLedger ledger : refundLedgers.values()) {
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("orderId", ledger.orderId);
            item.put("totalRefunded", money(ledger.totalRefunded));
            item.put("usedKeys", new ArrayList<String>(ledger.usedKeys));
            list.add(item);
        }
        return list;
    }

    private List<Map<String, Object>> resetAccountSnapshot() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (ResetAccount account : resetAccounts.values()) {
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("username", account.username);
            item.put("displayName", account.displayName);
            item.put("password", account.password);
            list.add(item);
        }
        return list;
    }

    private List<Map<String, Object>> approvalSnapshot() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (ApprovalTaskRecord task : approvalTasks.values()) {
            list.add(sanitizeApproval(task));
        }
        return list;
    }

    private List<Map<String, Object>> workflowOrderSnapshot() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (WorkflowOrderRecord order : workflowOrders.values()) {
            list.add(sanitizeWorkflowOrder(order));
        }
        return list;
    }

    private List<Map<String, Object>> inventorySnapshot() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (InventoryRecord inventory : inventoryStocks.values()) {
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("skuCode", inventory.skuCode);
            item.put("productName", inventory.productName);
            item.put("stock", inventory.stock);
            list.add(item);
        }
        return list;
    }

    private List<Map<String, Object>> walletSnapshot() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (WalletOrderRecord order : walletOrders.values()) {
            list.add(sanitizeWalletOrder(order));
        }
        return list;
    }

    private Map<String, Object> debugSnapshot() {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("taskId", debugTask.taskId);
        item.put("title", debugTask.title);
        item.put("status", debugTask.status);
        return item;
    }

    private List<Map<String, Object>> paymentSnapshot() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (PaymentOrderRecord order : paymentOrders.values()) {
            list.add(sanitizePaymentOrder(order));
        }
        return list;
    }

    private Map<String, Object> sanitizeApproval(ApprovalTaskRecord task) {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("taskId", task.taskId);
        item.put("title", task.title);
        item.put("creatorUserId", task.creatorUserId);
        item.put("status", task.status);
        item.put("history", new ArrayList<String>(task.history));
        return item;
    }

    private Map<String, Object> sanitizeWorkflowOrder(WorkflowOrderRecord order) {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("orderId", order.orderId);
        item.put("orderNumber", order.orderNumber);
        item.put("ownerUserId", order.ownerUserId);
        item.put("status", order.status);
        return item;
    }

    private Map<String, Object> sanitizeWalletOrder(WalletOrderRecord order) {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("orderId", order.orderId);
        item.put("orderNumber", order.orderNumber);
        item.put("ownerUserId", order.ownerUserId);
        item.put("amount", money(order.amount));
        item.put("refunded", order.refunded);
        item.put("usedRefundKeys", new ArrayList<String>(order.usedRefundKeys));
        return item;
    }

    private Map<String, Object> sanitizePaymentOrder(PaymentOrderRecord order) {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("orderNumber", order.orderNumber);
        item.put("amount", money(order.amount));
        item.put("status", order.status);
        item.put("merchantId", order.merchantId);
        item.put("lastCallbackAmount", money(order.lastCallbackAmount));
        return item;
    }

    private List<Map<String, String>> scenarioList() {
        List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        items.add(scenario("伪造身份", "POST /auth/login-vul", "客户端可控 debugUserId 与 bypassPassword 导致认证绕过"));
        items.add(scenario("水平越权", "GET /api/personal/{id}/vul", "普通用户可读取其他用户资料"));
        items.add(scenario("垂直越权", "GET /api/admin/report/vul", "服务端信任客户端角色头"));
        items.add(scenario("流程绕过", "POST /api/orders/{id}/checkout/vul", "可篡改金额并直接把订单标记为已支付"));
        items.add(scenario("短信验证码逻辑问题", "POST /sms/send-vul + POST /sms/verify-vul", "验证码回显、未绑定手机号、可复用"));
        items.add(scenario("短信轰炸", "POST /sms/bomb-vul", "缺少图形验证码和发送频控"));
        items.add(scenario("弱口令登录爆破", "GET /auth/bruteforce-vul", "SQLite 预置弱口令账号，无验证码和锁定"));
        items.add(scenario("图形验证码登录", "GET /auth/bruteforce-safe", "数字字母验证码、统一错误提示、失败临时锁定"));
        items.add(scenario("优惠券重复核销", "POST /promo/coupons/redeem/vul", "一次性优惠券可并发或重复使用"));
        items.add(scenario("重复退款", "POST /payments/{orderId}/refund/vul", "缺少幂等键与支付状态校验"));
        items.add(scenario("折扣叠加", "POST /pricing/discounts/calculate/vul", "优惠券、积分、会员折扣和闪购折扣可异常叠加"));
        items.add(scenario("密码重置 Token 复用", "POST /auth/reset/send-vul + POST /auth/reset/confirm-vul", "令牌可预测、可复用、未绑定目标账号"));
        items.add(scenario("审批流跳步", "POST /workflow/approval/{taskId}/vul", "客户端可直接指定目标审批状态"));
        items.add(scenario("订单状态机绕过", "POST /workflow/orders/{orderId}/state/vul", "订单可从 CREATED 直接跳到 COMPLETED"));
        items.add(scenario("负数金额套利", "POST /pricing/negative-amount/vul", "负数数量、负数金额或异常优惠可导致订单金额异常"));
        items.add(scenario("库存超卖", "POST /inventory/oversell/vul", "并发请求和非原子扣减会把库存扣成负数"));
        items.add(scenario("余额退款双花", "POST /wallet/orders/{orderId}/refund/vul", "钱包退款缺少幂等保护，余额可以被重复退回"));
        items.add(scenario("调试开关绕过", "POST /workflow/debug-bypass/vul", "调试参数 debugMode 或 skipAudit 被服务端直接信任"));
        items.add(scenario("伪造支付回调", "POST /payments/callback/vul", "只传 SUCCESS 状态就能把订单标记为已支付"));
        return items;
    }

    private Map<String, String> scenario(String name, String endpoint, String description) {
        Map<String, String> item = new LinkedHashMap<String, String>();
        item.put("name", name);
        item.put("endpoint", endpoint);
        item.put("description", description);
        return item;
    }

    private Map<String, Object> message(String text) {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("message", text);
        return data;
    }

    private List<Map<String, Object>> smsProfiles() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(smsUser(21L, "17365375549", "胡绍齐"));
        list.add(smsUser(23L, "13078470040", "金天翼"));
        list.add(smsUser(27L, "15134299958", "韩雨宁"));
        list.add(smsUser(29L, "15933988032", "贺修远"));
        return list;
    }

    private Map<String, Object> smsUser(Long userId, String phoneNumber, String name) {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("userId", userId);
        data.put("phoneNumber", phoneNumber);
        data.put("name", name);
        return data;
    }

    private String newCode() {
        return String.format("%06d", random.nextInt(1000000));
    }

    private int increaseCounter(Map<String, Integer> counterMap, String key) {
        int next = counterMap.containsKey(key) ? counterMap.get(key) + 1 : 1;
        counterMap.put(key, next);
        return next;
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private UserAccount findByPhone(String phoneNumber) {
        if ("17365375549".equals(phoneNumber)) {
            return users.get(21L);
        }
        if ("13078470040".equals(phoneNumber)) {
            return users.get(23L);
        }
        if ("15134299958".equals(phoneNumber)) {
            return users.get(27L);
        }
        if ("15933988032".equals(phoneNumber)) {
            return users.get(29L);
        }
        return null;
    }

    private double clampRate(double rate) {
        if (rate < 0) {
            return 0;
        }
        if (rate > 0.5) {
            return 0.5;
        }
        return rate;
    }

    private double clampMoney(double amount) {
        if (amount < 0) {
            return 0;
        }
        return amount;
    }

    private double money(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }

    private String paymentSign(String orderNumber, double amount, String merchantId) {
        return "SIGN-" + orderNumber + "-" + ((int) Math.round(amount * 100)) + "-" + merchantId;
    }

    private static final class CouponRecord {
        private final String code;
        private final double discountAmount;
        private int remaining;
        private final Set<Long> redeemedUserIds = new HashSet<Long>();

        private CouponRecord(String code, double discountAmount, int remaining) {
            this.code = code;
            this.discountAmount = discountAmount;
            this.remaining = remaining;
        }
    }

    private static final class RefundLedger {
        private final Long orderId;
        private double totalRefunded;
        private final Set<String> usedKeys = new HashSet<String>();

        private RefundLedger(Long orderId, double totalRefunded) {
            this.orderId = orderId;
            this.totalRefunded = totalRefunded;
        }
    }

    private static final class ResetAccount {
        private final String username;
        private final String displayName;
        private String password;

        private ResetAccount(String username, String displayName, String password) {
            this.username = username;
            this.displayName = displayName;
            this.password = password;
        }
    }

    private static final class ResetTokenRecord {
        private final String token;
        private final String username;
        private boolean used;
        private final long expiresAt;

        private ResetTokenRecord(String token, String username, long expiresAt) {
            this.token = token;
            this.username = username;
            this.expiresAt = expiresAt;
        }
    }

    private static final class ApprovalTaskRecord {
        private final Long taskId;
        private final String title;
        private final Long creatorUserId;
        private String status;
        private final List<String> history;

        private ApprovalTaskRecord(Long taskId, String title, Long creatorUserId, String status, List<String> history) {
            this.taskId = taskId;
            this.title = title;
            this.creatorUserId = creatorUserId;
            this.status = status;
            this.history = history;
        }
    }

    private static final class WorkflowOrderRecord {
        private final Long orderId;
        private final String orderNumber;
        private final Long ownerUserId;
        private String status;

        private WorkflowOrderRecord(Long orderId, String orderNumber, Long ownerUserId, String status) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.ownerUserId = ownerUserId;
            this.status = status;
        }
    }

    private static final class InventoryRecord {
        private final String skuCode;
        private final String productName;
        private int stock;

        private InventoryRecord(String skuCode, String productName, int stock) {
            this.skuCode = skuCode;
            this.productName = productName;
            this.stock = stock;
        }
    }

    private static final class WalletAccountRecord {
        private final Long userId;
        private double balance;

        private WalletAccountRecord(Long userId, double balance) {
            this.userId = userId;
            this.balance = balance;
        }
    }

    private static final class WalletOrderRecord {
        private final Long orderId;
        private final String orderNumber;
        private final Long ownerUserId;
        private final double amount;
        private boolean refunded;
        private final Set<String> usedRefundKeys = new HashSet<String>();

        private WalletOrderRecord(Long orderId, String orderNumber, Long ownerUserId, double amount, boolean refunded) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.ownerUserId = ownerUserId;
            this.amount = amount;
            this.refunded = refunded;
        }
    }

    private static final class DebugTaskRecord {
        private final Long taskId;
        private final String title;
        private String status;

        private DebugTaskRecord(Long taskId, String title, String status) {
            this.taskId = taskId;
            this.title = title;
            this.status = status;
        }
    }

    private static final class PaymentOrderRecord {
        private final String orderNumber;
        private final double amount;
        private String status;
        private final String merchantId;
        private double lastCallbackAmount;

        private PaymentOrderRecord(String orderNumber, double amount, String status, String merchantId) {
            this.orderNumber = orderNumber;
            this.amount = amount;
            this.status = status;
            this.merchantId = merchantId;
        }
    }

    private static final class SmsChallenge {
        private final String phoneNumber;
        private final String code;
        private boolean used;
        private int remainingAttempts;

        private SmsChallenge(String phoneNumber, String code, boolean used, int remainingAttempts) {
            this.phoneNumber = phoneNumber;
            this.code = code;
            this.used = used;
            this.remainingAttempts = remainingAttempts;
        }
    }
}
