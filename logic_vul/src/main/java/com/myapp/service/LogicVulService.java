package com.myapp.service;

import com.myapp.model.CheckoutRequest;
import com.myapp.model.LoginRequest;
import com.myapp.model.OrderRecord;
import com.myapp.model.PersonalProfile;
import com.myapp.model.SmsSendRequest;
import com.myapp.model.SmsVerifyRequest;
import com.myapp.model.UserAccount;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Random;

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
    private final Random random = new Random();

    public LogicVulService() {
        seedUsers();
        seedProfiles();
        seedOrders();
    }

    private void seedUsers() {
        users.put(21L, new UserAccount(21L, "jefferey.krajcik", "dbd9dv0k19z0mqy", "USER", "胡绍齐", "泽洋.龚@hotmail.com"));
        users.put(23L, new UserAccount(23L, "man.hackett", "oikihbi4xb", "USER", "金天翊", "果.洪@gmail.com"));
        users.put(27L, new UserAccount(27L, "frances.goldner", "3jwl2i3t6", "USER", "韩雨泽", "博涛.杨@yahoo.com"));
        users.put(29L, new UserAccount(29L, "yon.tremblay", "wyu3bxp4pc865s", "ADMIN", "贾修洁", "哲瀚.孟@hotmail.com"));
    }

    private void seedProfiles() {
        profiles.put(1L, new PersonalProfile(1L, 21L, "胡绍齐", "17365375549", "泽洋.龚@hotmail.com", "姚巷08号, 潍坊", "482867199511218036"));
        profiles.put(2L, new PersonalProfile(2L, 23L, "金天翊", "13078470040", "果.洪@gmail.com", "Apt. 902 张街43716号, 长春", "742462200007129678"));
        profiles.put(3L, new PersonalProfile(3L, 27L, "韩雨泽", "15134299958", "博涛.杨@yahoo.com", "陆旁3号, 福州", "346626200606101210"));
        profiles.put(4L, new PersonalProfile(4L, 29L, "贾修洁", "15933988032", "哲瀚.孟@hotmail.com", "冯街338号, 贵阳", "455866198905095417"));
    }

    private void seedOrders() {
        orders.put(5001L, new OrderRecord(5001L, "0526348562", 27L, "年度会员课程", 2, 99.90, "CREATED", true));
        orders.put(5002L, new OrderRecord(5002L, "0198213310", 23L, "企业分析报告", 1, 299.00, "CREATED", false));
        orders.put(5003L, new OrderRecord(5003L, "0515374436", 21L, "旗舰店优惠券包", 3, 19.90, "PAID", true));
    }

    public Map<String, Object> loginVulnerable(LoginRequest request) {
        UserAccount acting = null;
        String reason;
        if (request.getDebugUserId() != null) {
            acting = users.get(request.getDebugUserId());
            reason = "trusted debugUserId from client";
        } else {
            acting = findByUsername(request.getUsername());
            if (acting == null) {
                return message("用户不存在");
            }
            if (Boolean.TRUE.equals(request.getBypassPassword())) {
                reason = "trusted bypassPassword=true from client";
            } else if (acting.getPassword().equals(request.getPassword())) {
                reason = "password matched";
            } else {
                return message("密码错误，若设置 bypassPassword=true 仍可进入");
            }
        }
        if (acting == null) {
            return message("debugUserId 对应用户不存在");
        }
        return tokenResponse(acting, issueToken(acting.getId()), reason, true);
    }

    public Map<String, Object> loginSafe(LoginRequest request) {
        UserAccount acting = findByUsername(request.getUsername());
        if (acting == null || !acting.getPassword().equals(request.getPassword())) {
            return message("用户名或密码错误");
        }
        return tokenResponse(acting, issueToken(acting.getId()), "server-side password validation passed", false);
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
        data.put("viewerUserId", actingUserId);
        data.put("viewerName", acting == null ? "anonymous" : acting.getDisplayName());
        data.put("profile", profile);
        data.put("warning", "only used client-supplied user id, did not verify ownership");
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
        data.put("viewer", sanitizeUser(acting));
        data.put("profile", profile);
        return data;
    }

    public Map<String, Object> adminReportVulnerable(Long actingUserId, String roleFromClient) {
        if (!"ADMIN".equalsIgnoreCase(roleFromClient) && !"管理员".equals(roleFromClient)) {
            return message("把 X-Client-Role 改成 ADMIN 或 管理员 就能看到报表");
        }
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("actingUserId", actingUserId);
        data.put("roleSource", roleFromClient);
        data.put("report", buildAdminReport());
        data.put("warning", "server trusted client-controlled role header");
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
        double charged = request.getClientTotal() == null ? order.serverTotal() : request.getClientTotal();
        boolean markAsPaid = Boolean.TRUE.equals(request.getMarkAsPaid());
        if (markAsPaid) {
            order.setStatus("PAID");
        }
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("actingUserId", actingUserId);
        data.put("order", order);
        data.put("serverTotal", order.serverTotal());
        data.put("chargedTotal", charged);
        data.put("skipInventoryCheck", Boolean.TRUE.equals(request.getSkipInventoryCheck()));
        data.put("paymentReference", request.getPaymentReference());
        data.put("warning", "no ownership check and trusted clientTotal / markAsPaid");
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
            return message("库存未锁定，不能直接结算");
        }
        if (request == null || request.getPaymentReference() == null || request.getPaymentReference().trim().isEmpty()) {
            return message("缺少支付流水号");
        }
        order.setStatus("PAID");
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("viewer", sanitizeUser(acting));
        data.put("order", order);
        data.put("chargedTotal", order.serverTotal());
        data.put("paymentReference", request.getPaymentReference());
        data.put("message", "safe checkout used server-side total and ownership validation");
        return data;
    }

    public Map<String, Object> info() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("users", sanitizeUsers(users.values()));
        data.put("profiles", new ArrayList<PersonalProfile>(profiles.values()));
        data.put("orders", new ArrayList<OrderRecord>(orders.values()));
        data.put("smsUsers", smsProfiles());
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
        data.put("phoneNumber", request.getPhoneNumber());
        data.put("smsCode", code);
        data.put("warning", "验证码直接回显给前端，且旧验证码仍然有效");
        data.put("activeCodeCount", vulIssuedCodes.size());
        data.put("sendCount", sentCount);
        return data;
    }

    public Map<String, Object> verifySmsVulnerable(SmsVerifyRequest request) {
        if (request == null || blank(request.getPhoneNumber()) || blank(request.getSmsCode())) {
            return message("phoneNumber 和 smsCode 都不能为空");
        }
        SmsChallenge matched = null;
        for (SmsChallenge challenge : vulIssuedCodes) {
            if (request.getSmsCode().equals(challenge.code)) {
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
        data.put("targetPhone", request.getPhoneNumber());
        data.put("matchedCodeFromPhone", matched.phoneNumber);
        data.put("user", sanitizeUser(account));
        data.put("warning", "验证码未与手机号绑定，且验证成功后仍可重复使用");
        return data;
    }

    public Map<String, Object> sendSmsSafe(SmsSendRequest request) {
        if (request == null || blank(request.getPhoneNumber())) {
            return message("phoneNumber 不能为空");
        }
        int currentCount = smsSendCounterSafe.containsKey(request.getPhoneNumber()) ? smsSendCounterSafe.get(request.getPhoneNumber()) : 0;
        if (currentCount >= 3) {
            return message("发送过于频繁，安全版已触发限流");
        }
        String code = newCode();
        SmsChallenge challenge = new SmsChallenge(request.getPhoneNumber(), code, false, 5);
        smsChallengesSafe.put(request.getPhoneNumber(), challenge);
        int sentCount = increaseCounter(smsSendCounterSafe, request.getPhoneNumber());

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("phoneNumber", request.getPhoneNumber());
        data.put("message", "验证码已发送。安全版不会回显验证码，重发会覆盖旧码。");
        data.put("demoCode", code);
        data.put("sendCount", sentCount);
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
        int times = batch == null || batch.intValue() <= 0 ? 5 : batch.intValue();
        int allowed = 3;
        int current = smsSendCounterSafe.containsKey(phoneNumber) ? smsSendCounterSafe.get(phoneNumber) : 0;
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("phoneNumber", phoneNumber);
        data.put("requestedBatch", times);
        data.put("alreadySent", current);
        if (current >= allowed) {
            data.put("message", "安全版已命中频率限制，本轮未发送");
            data.put("remainingQuota", 0);
            return data;
        }
        int actual = Math.min(times, allowed - current);
        for (int i = 0; i < actual; i++) {
            increaseCounter(smsSendCounterSafe, phoneNumber);
        }
        data.put("actualSent", actual);
        data.put("remainingQuota", allowed - smsSendCounterSafe.get(phoneNumber));
        data.put("message", actual < times ? "安全版已拦截超出阈值的短信发送请求" : "安全版在阈值内放行");
        return data;
    }

    public Map<String, Object> verifySmsSafe(SmsVerifyRequest request) {
        if (request == null || blank(request.getPhoneNumber()) || blank(request.getSmsCode())) {
            return message("phoneNumber 和 smsCode 都不能为空");
        }
        SmsChallenge challenge = smsChallengesSafe.get(request.getPhoneNumber());
        if (challenge == null) {
            return message("请先发送验证码");
        }
        if (challenge.used) {
            return message("验证码已使用");
        }
        if (challenge.remainingAttempts <= 0) {
            return message("验证码尝试次数已耗尽");
        }
        if (!request.getSmsCode().equals(challenge.code)) {
            challenge.remainingAttempts--;
            return message("验证码错误，剩余尝试次数: " + challenge.remainingAttempts);
        }
        challenge.used = true;
        UserAccount account = findByPhone(request.getPhoneNumber());
        if (account == null) {
            return message("手机号未找到对应用户");
        }
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("targetPhone", request.getPhoneNumber());
        data.put("user", sanitizeUser(account));
        data.put("message", "验证码校验成功，当前验证码已作废");
        return data;
    }

    public String playgroundHtml() {
        return "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>logic_vul Playground</title>" + style() +
                "</head><body><div class=\"shell\"><div class=\"hero\"><h1>logic_vul Playground</h1><p>这个靶场覆盖伪造身份、水平越权、垂直越权和流程绕过。默认推荐先看 <code>/logic-vul/info</code>，再用下面按钮直接发请求。</p></div>" +
                card("伪造身份", "POST /auth/login-vul", "{\"username\":\"frances.goldner\",\"debugUserId\":29}", "send('POST','/auth/login-vul', '{\"username\":\"frances.goldner\",\"debugUserId\":29}')") +
                card("水平越权", "GET /api/personal/2/vul?actingUserId=27", "", "send('GET','/api/personal/2/vul?actingUserId=27','')") +
                card("垂直越权", "GET /api/admin/report/vul?actingUserId=27", "", "sendWithHeader('GET','/api/admin/report/vul?actingUserId=27','X-Client-Role','ADMIN')") +
                card("流程绕过", "POST /api/orders/5002/checkout/vul?actingUserId=27", "{\"clientTotal\":0.01,\"markAsPaid\":true,\"skipInventoryCheck\":true}", "send('POST','/api/orders/5002/checkout/vul?actingUserId=27', '{\"clientTotal\":0.01,\"markAsPaid\":true,\"skipInventoryCheck\":true}')") +
                card("短信码未绑定", "POST /sms/send-vul + /sms/verify-vul", "{\"phoneNumber\":\"15134299958\"}", "send('POST','/sms/send-vul', '{\"phoneNumber\":\"15134299958\"}')") +
                "<div class=\"panel\"><h2>响应</h2><pre id=\"result\">等待发送请求...</pre></div></div>" +
                "<script>const box=document.getElementById('result');async function send(method,url,body){box.textContent='请求发送中...';const opt={method:method,headers:{'Content-Type':'application/json'}};if(body){opt.body=body;}try{const res=await fetch(url,opt);box.textContent='HTTP '+res.status+'\\n'+await res.text();}catch(err){box.textContent='请求失败: '+err;}}async function sendWithHeader(method,url,key,val){box.textContent='请求发送中...';try{const res=await fetch(url,{method:method,headers:{[key]:val}});box.textContent='HTTP '+res.status+'\\n'+await res.text();}catch(err){box.textContent='请求失败: '+err;}}</script></body></html>";
    }

    private String card(String title, String path, String body, String action) {
        return "<div class=\"panel\"><h2>" + title + "</h2><p><code>" + path + "</code></p>" +
                (body.isEmpty() ? "" : "<pre>" + body + "</pre>") +
                "<button onclick=\"" + action.replace("\"", "&quot;") + "\">发送示例请求</button></div>";
    }

    private String style() {
        return "<style>body{margin:0;font-family:Arial,sans-serif;background:linear-gradient(135deg,#f5f3ea,#dce7f7);color:#1f2937}.shell{max-width:1100px;margin:0 auto;padding:36px 20px}.hero{background:#fff;padding:24px;border-radius:18px;box-shadow:0 10px 30px rgba(15,23,42,.08);margin-bottom:20px}.panel{background:#fff;padding:20px;border-radius:16px;box-shadow:0 10px 30px rgba(15,23,42,.08);margin-bottom:16px}h1,h2{margin:0 0 12px}pre{background:#0f172a;color:#e2e8f0;padding:14px;border-radius:12px;overflow:auto}button{background:#14532d;color:#fff;border:0;border-radius:999px;padding:10px 16px;cursor:pointer}code{background:#eef2ff;padding:2px 6px;border-radius:6px}</style>";
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

    private List<Map<String, String>> scenarioList() {
        List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        items.add(scenario("伪造身份", "POST /auth/login-vul", "通过 debugUserId 或 bypassPassword 直接拿到高权限 token"));
        items.add(scenario("水平越权", "GET /api/personal/{id}/vul", "登录用户可直接读取其他人的个人资料"));
        items.add(scenario("垂直越权", "GET /api/admin/report/vul", "接口只信任客户端传入的角色头"));
        items.add(scenario("流程绕过", "POST /api/orders/{id}/checkout/vul", "可伪造价格并直接把订单标记为已支付"));
        items.add(scenario("短信验证码逻辑问题", "POST /sms/send-vul + POST /sms/verify-vul", "验证码回显、未与手机号绑定、重复使用和缺少尝试限制"));
        items.add(scenario("短信轰炸", "POST /sms/bomb-vul", "缺少发送频率限制，可被批量刷短信"));
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
        list.add(smsUser(23L, "13078470040", "金天翊"));
        list.add(smsUser(27L, "15134299958", "韩雨泽"));
        list.add(smsUser(29L, "15933988032", "贾修洁"));
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

    private int increaseCounter(Map<String, Integer> counterMap, String phoneNumber) {
        int next = counterMap.containsKey(phoneNumber) ? counterMap.get(phoneNumber) + 1 : 1;
        counterMap.put(phoneNumber, next);
        return next;
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
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

    private static class SmsChallenge {
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
