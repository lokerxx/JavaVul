package com.myapp.jshook;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.myapp.jshook.proto.api.ApiRequest;
import com.myapp.jshook.proto.api.ApiResponse;
import com.myapp.jshook.proto.api.OrderInfo;
import com.myapp.jshook.proto.api.ProductInfo;
import com.myapp.jshook.proto.api.UserInfo;
import com.myapp.jshook.proto.microservice.AnalyticsResponse;
import com.myapp.jshook.proto.microservice.NotificationResponse;
import com.myapp.jshook.proto.microservice.OrderResponse;
import com.myapp.jshook.proto.microservice.ServiceRequest;
import com.myapp.jshook.proto.microservice.ServiceResponse;
import com.myapp.jshook.proto.microservice.UserResponse;
import com.myapp.jshook.proto.report.ChartData;
import com.myapp.jshook.proto.report.DataPoint;
import com.myapp.jshook.proto.report.DataResponse;
import com.myapp.jshook.proto.report.ReportData;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsHookController {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final SecureRandom RANDOM = new SecureRandom();

    @GetMapping("/api/items")
    public ResponseEntity<?> items(@RequestParam("sign") String sign) {
        String expected = CryptoJsCompat.hmacSha256Hex("/api/items", "my-secret-key");
        if (!expected.equals(sign)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(error("Invalid sign", "expected", expected, "received", sign));
        }
        return ResponseEntity.ok(mapOf(
            "items", Arrays.asList(item(1, "Item 1"), item(2, "Item 2"), item(3, "Item 3"))
        ));
    }

    @GetMapping("/api/search-products")
    public ResponseEntity<?> searchProducts(@RequestParam("q") String encryptedQuery) {
        try {
            String decrypted = CryptoJsCompat.decrypt(encryptedQuery, "query-encrypt-key-2025");
            Map<String, Object> params = MAPPER.readValue(decrypted, new TypeReference<Map<String, Object>>() {});
            String keyword = stringValue(params.get("keyword"));
            String category = stringValue(params.get("category"));
            int minPrice = intValue(params.get("minPrice"), 0);
            int maxPrice = intValue(params.get("maxPrice"), 999999);

            List<Map<String, Object>> products = defaultProducts().stream()
                .filter(product -> !StringUtils.hasText(keyword) || stringValue(product.get("name")).contains(keyword))
                .filter(product -> !StringUtils.hasText(category) || category.equals(product.get("category")))
                .filter(product -> intValue(product.get("price"), 0) >= minPrice && intValue(product.get("price"), 0) <= maxPrice)
                .collect(Collectors.toList());

            return ResponseEntity.ok(mapOf("products", products, "searchParams", params, "total", products.size()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(error("Invalid encrypted parameters", "details", ex.getMessage()));
        }
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        String username = stringValue(body.get("username"));
        String password = stringValue(body.get("password"));
        String timestamp = stringValue(body.get("timestamp"));
        String sign = stringValue(body.get("sign"));
        String expected = CryptoJsCompat.hmacSha256Hex(username + password + timestamp, "form-encrypt-key-2025");
        if (!expected.equals(sign)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error("Invalid signature"));
        }
        try {
            String decryptedPassword = CryptoJsCompat.decrypt(password, "form-encrypt-key-2025");
            List<Map<String, String>> users = Arrays.asList(
                user("admin@example.com", "123456", "管理员"),
                user("user@example.com", "password", "普通用户"),
                user("test", "test123", "测试用户")
            );
            Map<String, String> matched = users.stream()
                .filter(u -> (u.get("username").equals(username) || u.get("username").split("@")[0].equals(username))
                    && u.get("password").equals(decryptedPassword))
                .findFirst()
                .orElse(null);
            if (matched == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("用户名或密码错误"));
            }
            return ResponseEntity.ok(mapOf(
                "success", true,
                "message", "登录成功",
                "user", mapOf("id", users.indexOf(matched) + 1, "username", matched.get("name"), "email", matched.get("username")),
                "token", CryptoJsCompat.hmacSha256Hex(String.valueOf(users.indexOf(matched) + 1) + System.currentTimeMillis(), "token-secret"),
                "loginTime", Instant.now().toString(),
                "rememberMe", body.get("rememberMe")
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(error("Invalid encrypted data", "details", ex.getMessage()));
        }
    }

    @PostMapping("/api/submit-user-info")
    public ResponseEntity<?> submitUserInfo(@RequestBody Map<String, Object> body) {
        try {
            String phone = CryptoJsCompat.decrypt(stringValue(body.get("phone")), "json-field-encrypt-2025");
            String idCard = CryptoJsCompat.decrypt(stringValue(body.get("idCard")), "json-field-encrypt-2025");
            String bankCard = CryptoJsCompat.decrypt(stringValue(body.get("bankCard")), "json-field-encrypt-2025");
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return ResponseEntity.badRequest().body(error("手机号格式不正确"));
            }
            if (!idCard.matches("^\\d{17}[\\dX]$")) {
                return ResponseEntity.badRequest().body(error("身份证号格式不正确"));
            }
            if (!bankCard.matches("^\\d{16,19}$")) {
                return ResponseEntity.badRequest().body(error("银行卡号格式不正确"));
            }
            return ResponseEntity.ok(mapOf(
                "success", true,
                "message", "用户信息提交成功",
                "userId", 10000 + RANDOM.nextInt(90000),
                "submitTime", Instant.now().toString(),
                "status", "已处理",
                "decryptedData", mapOf(
                    "phone", phone,
                    "idCard", idCard.replaceAll("(\\d{6})\\d{8}(\\d{4})", "$1********$2"),
                    "bankCard", bankCard.replaceAll("(\\d{4})\\d{8,11}(\\d{4})", "$1****$2")
                ),
                "userInfo", mapOf(
                    "name", body.get("name"),
                    "email", body.get("email"),
                    "city", body.get("city"),
                    "age", body.get("age"),
                    "remarks", body.get("remarks")
                )
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(error("解密失败", "details", ex.getMessage()));
        }
    }

    @GetMapping("/api/user-details/{userId}")
    public ResponseEntity<?> userDetails(@PathVariable("userId") String userId) {
        Map<String, Map<String, Object>> users = userDetailsData();
        Map<String, Object> user = users.get(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("用户不存在"));
        }
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("id", user.get("id"));
        data.put("name", user.get("name"));
        data.put("email", user.get("email"));
        data.put("department", user.get("department"));
        data.put("encryptedPhone", CryptoJsCompat.encrypt(stringValue(user.get("phone")), "response-decrypt-2025"));
        data.put("encryptedIdCard", CryptoJsCompat.encrypt(stringValue(user.get("idCard")), "response-decrypt-2025"));
        data.put("encryptedBankCard", CryptoJsCompat.encrypt(stringValue(user.get("bankCard")), "response-decrypt-2025"));
        data.put("encryptedAddress", CryptoJsCompat.encrypt(stringValue(user.get("address")), "response-decrypt-2025"));
        data.put("createdAt", user.get("createdAt"));
        data.put("lastLogin", user.get("lastLogin"));
        data.put("status", user.get("status"));
        return ResponseEntity.ok(mapOf("success", true, "message", "获取用户信息成功", "data", data, "timestamp", Instant.now().toString()));
    }

    @PostMapping("/api/send-message")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> body) {
        try {
            String message = CryptoJsCompat.decrypt(stringValue(body.get("encryptedMessage")), "single-field-2025");
            String reply = randomFrom(Arrays.asList("消息已收到，谢谢。", "收到您的消息，正在处理中。", "感谢您的留言，我们会尽快回复。", "系统已记录您的消息。"));
            return ResponseEntity.ok(mapOf(
                "success", true,
                "message", "消息发送成功",
                "messageId", randomId("MSG"),
                "sender", body.get("sender"),
                "timestamp", Instant.now().toString(),
                "encryptedContent", CryptoJsCompat.encrypt(reply, "single-field-2025"),
                "originalMessage", message
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(error("消息处理失败", "details", ex.getMessage()));
        }
    }

    @PostMapping(value = "/api/secure-submit", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> secureSubmit(@RequestBody String hexBody) {
        try {
            Map<String, Object> data = decryptHexBody(hexBody, "hex-body-encrypt-2025");
            return ResponseEntity.ok(mapOf(
                "success", true,
                "message", "数据提交成功",
                "submissionId", randomId("SUB"),
                "status", "已接收并处理",
                "timestamp", Instant.now().toString(),
                "securityLevel", "HIGHEST",
                "decryptedData", mapOf(
                    "companyName", data.get("companyName"),
                    "contactPerson", data.get("contactPerson"),
                    "budget", data.get("budget"),
                    "urgency", data.get("urgency"),
                    "industry", data.get("industry")
                ),
                "processingInfo", mapOf(
                    "hexDataLength", hexBody.length(),
                    "encryptedDataLength", new String(CryptoJsCompat.fromHex(hexBody), StandardCharsets.UTF_8).length(),
                    "originalDataSize", MAPPER.writeValueAsString(data).length()
                )
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(error("请求体处理失败", "details", ex.getMessage()));
        }
    }

    @GetMapping(value = "/api/secure-query/{type}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> secureQuery(@PathVariable("type") String type) {
        Map<String, Object> data = secureQueryData().get(type);
        if (data == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not_found");
        }
        String encrypted = CryptoJsCompat.encrypt(MAPPERValue(data), "hex-response-decrypt-2025");
        return ResponseEntity.ok(CryptoJsCompat.toHex(encrypted.getBytes(StandardCharsets.UTF_8)));
    }

    @PostMapping(value = "/api/secure-operation", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> secureOperation(@RequestBody String hexBody) {
        try {
            Map<String, Object> data = decryptHexBody(hexBody, "bidirectional-hex-2025");
            Map<String, Object> response = mapOf(
                "success", true,
                "operationId", randomId("OP"),
                "operation", data.get("operation"),
                "status", "执行成功",
                "executionTime", Instant.now().toString(),
                "securityLevel", "TOP_SECRET",
                "requestId", data.get("requestId"),
                "details", secureOperationDetails(data)
            );
            String encrypted = CryptoJsCompat.encrypt(MAPPERValue(response), "bidirectional-hex-2025");
            return ResponseEntity.ok(CryptoJsCompat.toHex(encrypted.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(CryptoJsCompat.toHex(("error:" + ex.getMessage()).getBytes(StandardCharsets.UTF_8)));
        }
    }

    @PostMapping(value = "/api/protobuf", consumes = "application/x-protobuf", produces = "application/x-protobuf")
    public ResponseEntity<byte[]> protobuf(@RequestBody byte[] payload) throws InvalidProtocolBufferException {
        ApiRequest request = ApiRequest.parseFrom(payload);
        ApiResponse.Builder response = ApiResponse.newBuilder()
            .setRequestId(request.getRequestId())
            .setTimestamp(Instant.now().getEpochSecond())
            .setSuccess(true)
            .setMessage("Protocol Buffers request handled successfully")
            .setCode(200);

        switch (request.getOperation()) {
            case "user":
                if (request.hasUserInfo()) {
                    UserInfo user = request.getUserInfo();
                    response.setUserInfo(user.toBuilder()
                        .putMetadata("user_id", randomId("USR"))
                        .putMetadata("created_at", Instant.now().toString())
                        .putMetadata("status", "active")
                        .build());
                }
                break;
            case "product":
                if (request.hasProductInfo()) {
                    ProductInfo product = request.getProductInfo();
                    response.setProductInfo(product.toBuilder()
                        .putAttributes("product_id", randomId("PRD"))
                        .putAttributes("created_at", Instant.now().toString())
                        .putAttributes("status", "available")
                        .build());
                }
                break;
            case "order":
                if (request.hasOrderInfo()) {
                    OrderInfo order = request.getOrderInfo();
                    response.setOrderInfo(order.toBuilder().setStatus("confirmed").setCreatedAt(Instant.now().getEpochSecond()).build());
                }
                break;
            default:
                response.setSuccess(false).setCode(400).setMessage("Unsupported operation");
        }
        return protobufResponse(response.build().toByteArray());
    }

    @GetMapping(value = "/api/protobuf-response", produces = "application/x-protobuf")
    public ResponseEntity<byte[]> protobufResponseApi(@RequestParam("category") String category, @RequestParam("option") String option) {
        long now = Instant.now().getEpochSecond();
        ReportData report = ReportData.newBuilder()
            .setReportId(randomId("RPT"))
            .setTitle(category + " " + option + " report")
            .setDescription("Generated by Spring Boot JS-hook backend")
            .addAllCharts(defaultCharts(option, now))
            .putAllSummaryMetrics(defaultSummaryMetrics(option))
            .setGeneratedAt(now)
            .build();
        DataResponse response = DataResponse.newBuilder()
            .setRequestId(randomId("REQ"))
            .setTimestamp(now)
            .setSuccess(true)
            .setMessage(category + "-" + option + " data fetched successfully")
            .setCode(200)
            .setCategory(category)
            .setOption(option)
            .setReportData(report)
            .build();
        return protobufResponse(response.toByteArray());
    }

    @PostMapping(value = "/api/bidirectional-protobuf", consumes = "application/x-protobuf", produces = "application/x-protobuf")
    public ResponseEntity<byte[]> bidirectionalProtobuf(@RequestBody byte[] payload) throws InvalidProtocolBufferException {
        ServiceRequest request = ServiceRequest.parseFrom(payload);
        long now = Instant.now().getEpochSecond();
        ServiceResponse.Builder response = ServiceResponse.newBuilder()
            .setRequestId(request.getRequestId())
            .setTimestamp(now)
            .setSuccess(true)
            .setServiceName(request.getServiceName())
            .setStatusCode(200);

        switch (request.getServiceName()) {
            case "user-management":
                if (request.hasUserRequest()) {
                    response.setUserResponse(UserResponse.newBuilder()
                        .setSuccess(true).setMessage("User operation succeeded")
                        .setUserId(request.getUserRequest().getUserId()).setName(request.getUserRequest().getName())
                        .setEmail(request.getUserRequest().getEmail()).setRole(request.getUserRequest().getRole())
                        .setStatus(request.getUserRequest().getStatus()).setCreatedAt(now).setUpdatedAt(now).build());
                }
                break;
            case "order-processing":
                if (request.hasOrderRequest()) {
                    response.setOrderResponse(OrderResponse.newBuilder()
                        .setSuccess(true).setMessage("Order operation succeeded")
                        .setOrderId(request.getOrderRequest().getOrderId()).setCustomerId(request.getOrderRequest().getCustomerId())
                        .setAmount(request.getOrderRequest().getAmount()).setPaymentMethod(request.getOrderRequest().getPaymentMethod())
                        .setStatus("pending".equals(request.getOrderRequest().getStatus()) ? "processing" : request.getOrderRequest().getStatus())
                        .setCreatedAt(now).setTrackingNumber(randomId("TRK")).build());
                }
                break;
            case "data-analytics":
                if (request.hasAnalyticsRequest()) {
                    response.setAnalyticsResponse(AnalyticsResponse.newBuilder()
                        .setSuccess(true).setMessage("Analytics completed")
                        .setReportId(randomId("RPT")).setAnalyticsType(request.getAnalyticsRequest().getAnalyticsType())
                        .putAllMetrics(defaultSummaryMetrics(request.getAnalyticsRequest().getAnalyticsType()))
                        .setDownloadUrl("https://reports.example.com/download/" + UUID.randomUUID().toString().replace("-", ""))
                        .setGeneratedAt(now).build());
                }
                break;
            case "notification":
                if (request.hasNotificationRequest()) {
                    response.setNotificationResponse(NotificationResponse.newBuilder()
                        .setSuccess(true).setMessage("Notification sent")
                        .setNotificationId(randomId("NOT")).setStatus("sent").setSentAt(now).setDeliveryStatus("delivered").build());
                }
                break;
            default:
                response.setSuccess(false).setStatusCode(400);
        }
        return protobufResponse(response.build().toByteArray());
    }

    @PostMapping("/api/header-sign")
    public ResponseEntity<?> headerSign(
        @RequestBody Map<String, Object> body,
        @RequestHeader(value = "X-Sign", required = false) String sign,
        @RequestHeader(value = "X-Timestamp", required = false) String timestamp,
        @RequestHeader(value = "X-Nonce", required = false) String nonce,
        @RequestHeader(value = "X-Client-Id", required = false) String clientId
    ) {
        if (!StringUtils.hasText(sign) || !StringUtils.hasText(timestamp) || !StringUtils.hasText(nonce) || !StringUtils.hasText(clientId)) {
            return ResponseEntity.badRequest().body(error("缺少必要的签名请求头"));
        }
        if (!verifyHeaderSignature(body, timestamp, nonce, sign, "your-secret-key-2025")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("签名验证失败", "signature_valid", false));
        }
        Map<String, Object> response = mapOf(
            "request_id", randomId("REQ"),
            "timestamp", Instant.now().getEpochSecond(),
            "signature_valid", true,
            "api_type", body.get("api_type"),
            "client_id", clientId
        );
        String apiType = stringValue(body.get("api_type"));
        if ("payment".equals(apiType)) {
            response.put("payment_result", mapOf("status", "success", "transaction_id", randomId("TXN"), "amount", body.get("amount"), "payment_method", body.get("payment_method"), "fee", String.format("%.2f", doubleValue(body.get("amount")) * 0.006), "order_id", body.get("order_id"), "merchant_id", body.get("merchant_id")));
        } else if ("transfer".equals(apiType)) {
            response.put("transfer_result", mapOf("status", "processing", "transfer_id", randomId("TRF"), "amount", body.get("amount"), "currency", body.get("currency"), "from_account", body.get("from_account"), "to_account", body.get("to_account"), "estimated_arrival", "2-24小时内到账"));
        } else if ("sensitive".equals(apiType)) {
            response.put("access_result", mapOf("status", "granted", "data_type", body.get("data_type"), "access_level", body.get("access_level"), "user_id", body.get("user_id"), "department", body.get("department"), "access_token", randomId("AT"), "expires_in", 3600));
        } else if ("admin".equals(apiType)) {
            response.put("admin_result", mapOf("status", "authorized", "action", body.get("action"), "admin_level", body.get("admin_level"), "admin_id", body.get("admin_id"), "operation_id", randomId("OP"), "audit_log", "管理员执行操作已记录", "session_id", body.get("session_id")));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/response-header-cookie")
    public ResponseEntity<?> responseHeaderCookie(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = mapOf(
            "session_id", randomId("SES"),
            "timestamp", Instant.now().getEpochSecond(),
            "authenticated", true,
            "service_type", body.get("service_type"),
            "client_ip", body.get("client_ip")
        );
        Map<String, Object> cookieData = new LinkedHashMap<String, Object>();
        cookieData.put("user_id", randomId("USER"));
        cookieData.put("session_token", randomId("TOKEN"));
        cookieData.put("permission_level", "standard");
        cookieData.put("expires_at", Instant.now().getEpochSecond() + 86400);
        cookieData.put("device_info", body.get("device_type"));
        cookieData.put("last_activity", Instant.now().getEpochSecond());

        String serviceType = stringValue(body.get("service_type"));
        if ("login".equals(serviceType)) {
            response.put("login_result", mapOf("status", "success", "user_id", cookieData.get("user_id"), "access_token", randomId("AT"), "token_type", "Bearer", "expires_in", 3600));
            cookieData.put("permission_level", "authenticated");
            cookieData.put("remember_me", body.get("remember"));
        } else if ("oauth".equals(serviceType)) {
            response.put("oauth_result", mapOf("status", "authorized", "provider", body.get("provider"), "access_token", randomId("OAT"), "scope", body.get("scope"), "user_info", "oauth_user"));
            cookieData.put("permission_level", "oauth");
        } else if ("sso".equals(serviceType)) {
            response.put("sso_result", mapOf("status", "authenticated", "provider", body.get("sso_provider"), "user_identifier", "domain\\user", "domain", body.get("domain"), "service_ticket", randomId("ST")));
            cookieData.put("permission_level", "sso");
        } else if ("refresh".equals(serviceType)) {
            response.put("refresh_result", mapOf("status", "refreshed", "new_access_token", randomId("RAT"), "new_refresh_token", randomId("RRT"), "expires_in", expirySeconds(stringValue(body.get("expiry"))), "scope", body.get("scope")));
            cookieData.put("permission_level", "refreshed");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Cookie", CryptoJsCompat.encrypt(MAPPERValue(cookieData), "cookie-secret-key-2025"));
        headers.add("X-Session-Id", stringValue(response.get("session_id")));
        headers.add("X-Auth-Status", "success");
        headers.add("X-Service-Type", serviceType);
        return new ResponseEntity<Object>(response, headers, HttpStatus.OK);
    }

    @PostMapping("/api/interceptor-user-service")
    public ResponseEntity<?> interceptorUser(@RequestBody Map<String, Object> body) {
        return interceptor("user-service", body);
    }

    @PostMapping("/api/interceptor-order-service")
    public ResponseEntity<?> interceptorOrder(@RequestBody Map<String, Object> body) {
        return interceptor("order-service", body);
    }

    @PostMapping("/api/interceptor-payment-service")
    public ResponseEntity<?> interceptorPayment(@RequestBody Map<String, Object> body) {
        return interceptor("payment-service", body);
    }

    @PostMapping("/api/interceptor-inventory-service")
    public ResponseEntity<?> interceptorInventory(@RequestBody Map<String, Object> body) {
        return interceptor("inventory-service", body);
    }

    @PostMapping("/api/interceptor-analytics-service")
    public ResponseEntity<?> interceptorAnalytics(@RequestBody Map<String, Object> body) {
        return interceptor("analytics-service", body);
    }

    @PostMapping("/api/interceptor-notification-service")
    public ResponseEntity<?> interceptorNotification(@RequestBody Map<String, Object> body) {
        return interceptor("notification-service", body);
    }

    @GetMapping("/api/video-segment/{videoType}/{segmentId}")
    public ResponseEntity<?> videoSegment(@PathVariable("videoType") String videoType, @PathVariable("segmentId") int segmentId) {
        Map<String, Object> config = videoConfigs().get(videoType);
        if (config == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("未知的视频类型"));
        }
        int segmentCount = intValue(config.get("segmentCount"), 0);
        if (segmentId < 0 || segmentId >= segmentCount) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("片段索引超出范围"));
        }
        try {
            byte[] key = Arrays.copyOf(CryptoJsCompat.sha256Bytes(stringValue(config.get("encryptionKey"))), 16);
            byte[] iv = new byte[16];
            RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(generateVideoSegmentData(videoType, segmentId).getBytes(StandardCharsets.UTF_8));
            return ResponseEntity.ok(mapOf(
                "video_type", videoType,
                "segment_id", segmentId,
                "segment_name", String.format("segment_%03d.ts", segmentId),
                "encrypted_data", Base64.getEncoder().encodeToString(encrypted),
                "iv", CryptoJsCompat.toHex(iv),
                "encryption_method", "AES-128-CBC",
                "segment_size", encrypted.length,
                "duration", 30,
                "timestamp", Instant.now().getEpochSecond(),
                "content_type", "video/mp2t"
            ));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(error("视频片段处理失败", "details", ex.getMessage()));
        }
    }

    @PostMapping("/api/video-segments/batch")
    public ResponseEntity<?> videoSegmentsBatch(@RequestBody Map<String, Object> body) {
        String videoType = stringValue(body.get("video_type"));
        List<Object> ids = body.get("segment_ids") instanceof List ? (List<Object>) body.get("segment_ids") : Collections.emptyList();
        List<Object> segments = new ArrayList<Object>();
        List<Object> errors = new ArrayList<Object>();
        for (Object id : ids) {
            try {
                int segmentId = intValue(id, -1);
                ResponseEntity<?> response = videoSegment(videoType, segmentId);
                if (response.getStatusCode().is2xxSuccessful()) {
                    segments.add(response.getBody());
                } else {
                    errors.add(mapOf("segment_id", segmentId, "error", "segment failed"));
                }
            } catch (Exception ex) {
                errors.add(mapOf("segment_id", id, "error", ex.getMessage()));
            }
        }
        return ResponseEntity.ok(mapOf("video_type", videoType, "total_requested", ids.size(), "successful_segments", segments.size(), "failed_segments", errors.size(), "segments", segments, "errors", errors, "timestamp", Instant.now().getEpochSecond()));
    }

    private ResponseEntity<?> interceptor(String serviceName, Map<String, Object> body) {
        if (!verifyInterceptorSignature(body, "interceptor-secret-key-2025")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("拦截器签名验证失败", "signature_valid", false));
        }
        return ResponseEntity.ok(mapOf(
            "request_id", randomId("REQ"),
            "timestamp", Instant.now().getEpochSecond(),
            "signature_valid", true,
            "service_name", serviceName,
            "interceptor_id", body.get("interceptor_id"),
            "client_id", body.get("client_id"),
            "service_result", interceptorResult(serviceName)
        ));
    }

    private boolean verifyHeaderSignature(Map<String, Object> body, String timestamp, String nonce, String signature, String secret) {
        String paramString = body.keySet().stream().sorted().map(key -> key + "=" + stringValue(body.get(key))).collect(Collectors.joining("&"));
        return CryptoJsCompat.hmacSha256Hex(paramString + "&timestamp=" + timestamp + "&nonce=" + nonce + "&key=" + secret, secret).equals(signature);
    }

    private boolean verifyInterceptorSignature(Map<String, Object> body, String secret) {
        Map<String, Object> signData = new LinkedHashMap<String, Object>(body);
        String sign = stringValue(signData.remove("sign"));
        String paramString = signData.keySet().stream().sorted().map(key -> key + "=" + stringValue(signData.get(key))).collect(Collectors.joining("&"));
        return CryptoJsCompat.md5Hex(paramString + "&key=" + secret).equals(sign);
    }

    private Map<String, Object> decryptHexBody(String hexBody, String secret) throws Exception {
        String encrypted = new String(CryptoJsCompat.fromHex(hexBody), StandardCharsets.UTF_8);
        String json = CryptoJsCompat.decrypt(encrypted, secret);
        return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
    }

    private static ResponseEntity<byte[]> protobufResponse(byte[] payload) {
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/x-protobuf")).body(payload);
    }

    private static Map<String, Object> item(int id, String name) {
        return mapOf("id", id, "name", name);
    }

    private static Map<String, String> user(String username, String password, String name) {
        Map<String, String> user = new LinkedHashMap<String, String>();
        user.put("username", username);
        user.put("password", password);
        user.put("name", name);
        return user;
    }

    private static Map<String, Object> error(String message, Object... extra) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("error", message);
        for (int i = 0; i + 1 < extra.length; i += 2) {
            map.put(String.valueOf(extra[i]), extra[i + 1]);
        }
        return map;
    }

    private static Map<String, Object> mapOf(Object... pairs) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            map.put(String.valueOf(pairs[i]), pairs[i + 1]);
        }
        return map;
    }

    private static String randomId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private static String randomFrom(List<String> values) {
        return values.get(RANDOM.nextInt(values.size()));
    }

    private static String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static int intValue(Object value, int defaultValue) {
        try {
            return value == null ? defaultValue : (int) Math.round(Double.parseDouble(String.valueOf(value)));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    private static double doubleValue(Object value) {
        try {
            return value == null ? 0D : Double.parseDouble(String.valueOf(value));
        } catch (Exception ex) {
            return 0D;
        }
    }

    private static String MAPPERValue(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static List<Map<String, Object>> defaultProducts() {
        return Arrays.asList(
            mapOf("id", 1, "name", "iPhone", "price", 6999, "category", "electronics"),
            mapOf("id", 2, "name", "Android Phone", "price", 4999, "category", "electronics"),
            mapOf("id", 3, "name", "Budget Phone", "price", 2999, "category", "electronics"),
            mapOf("id", 4, "name", "T-Shirt", "price", 199, "category", "clothing"),
            mapOf("id", 5, "name", "Jeans", "price", 299, "category", "clothing"),
            mapOf("id", 6, "name", "JavaScript Book", "price", 89, "category", "books"),
            mapOf("id", 7, "name", "Vue Guide", "price", 79, "category", "books"),
            mapOf("id", 8, "name", "Desk Lamp", "price", 299, "category", "home"),
            mapOf("id", 9, "name", "Bluetooth Speaker", "price", 399, "category", "electronics"),
            mapOf("id", 10, "name", "Running Shoes", "price", 599, "category", "clothing")
        );
    }

    private static Map<String, Map<String, Object>> userDetailsData() {
        Map<String, Map<String, Object>> users = new LinkedHashMap<String, Map<String, Object>>();
        users.put("1001", mapOf("id", 1001, "name", "Alice", "email", "alice@company.com", "department", "engineering", "phone", "13800138001", "idCard", "110101199001011001", "bankCard", "6222021234567890001", "address", "Beijing Example Road 123", "createdAt", "2023-01-15T08:30:00Z", "lastLogin", "2025-01-31T10:15:00Z", "status", "active"));
        users.put("1002", mapOf("id", 1002, "name", "Bob", "email", "bob@company.com", "department", "marketing", "phone", "13800138002", "idCard", "110101199002022002", "bankCard", "6222021234567890002", "address", "Shanghai Example Road 456", "createdAt", "2023-02-20T09:45:00Z", "lastLogin", "2025-01-31T09:30:00Z", "status", "active"));
        users.put("1003", mapOf("id", 1003, "name", "Carol", "email", "carol@company.com", "department", "finance", "phone", "13800138003", "idCard", "110101199003033003", "bankCard", "6222021234567890003", "address", "Guangzhou Example Avenue 789", "createdAt", "2023-03-10T14:20:00Z", "lastLogin", "2025-01-30T16:45:00Z", "status", "active"));
        users.put("1004", mapOf("id", 1004, "name", "Dave", "email", "dave@company.com", "department", "hr", "phone", "13800138004", "idCard", "110101199004044004", "bankCard", "6222021234567890004", "address", "Shenzhen Science Park 101", "createdAt", "2023-04-05T11:10:00Z", "lastLogin", "2025-01-29T14:20:00Z", "status", "active"));
        return users;
    }

    private static Map<String, Map<String, Object>> secureQueryData() {
        Map<String, Map<String, Object>> data = new LinkedHashMap<String, Map<String, Object>>();
        data.put("financial", mapOf("type", "financial", "reportType", "annual_report", "period", "2024", "revenue", 15680000, "profit", 3420000, "assets", 45600000, "liabilities", 12300000, "timestamp", Instant.now().toString(), "securityLevel", "confidential"));
        data.put("employee", mapOf("type", "employee", "name", "Ethan", "employeeId", "EMP001234", "department", "engineering", "position", "senior_engineer", "salary", 25000, "bonus", 50000, "socialSecurity", "paid", "timestamp", Instant.now().toString(), "securityLevel", "confidential"));
        data.put("customer", mapOf("type", "customer", "companyName", "Tech Innovation Group", "customerId", "CUST789012", "contactPerson", "Wang", "phone", "13800138000", "email", "wang@techgroup.com", "annualRevenue", 8900000, "creditRating", "AAA", "timestamp", Instant.now().toString(), "securityLevel", "confidential"));
        data.put("project", mapOf("type", "project", "projectName", "Smart Data Platform", "projectId", "PROJ456789", "manager", "Manager Zhang", "budget", 5600000, "spent", 3200000, "progress", 68, "startDate", "2024-01-15", "expectedEnd", "2025-06-30", "timestamp", Instant.now().toString(), "securityLevel", "confidential"));
        return data;
    }

    private static Map<String, Object> secureOperationDetails(Map<String, Object> data) {
        String operation = stringValue(data.get("operation"));
        if ("transfer".equals(operation)) {
            return mapOf("amount", data.get("amount"), "fee", Math.round(doubleValue(data.get("amount")) * 0.001), "transactionId", randomId("TXN"), "fromAccount", data.get("fromAccount"), "toAccount", data.get("toAccount"), "currency", data.get("currency"), "estimatedArrival", "2-24h");
        }
        if ("contract".equals(operation)) {
            return mapOf("contractNumber", randomId("CON"), "signatureStatus", "signed", "legalStatus", "effective", "digitalSignature", "SHA256:" + UUID.randomUUID().toString().replace("-", "").substring(0, 16), "contractValue", data.get("value"), "effectiveDate", LocalDate.now().toString());
        }
        if ("audit".equals(operation)) {
            return mapOf("reportId", randomId("AUD"), "issuesFound", 1 + RANDOM.nextInt(5), "riskLevel", randomFrom(Arrays.asList("low", "medium", "high")), "auditScore", 80 + RANDOM.nextInt(20), "recommendations", "Strengthen password policy and access control", "nextAuditDate", LocalDate.now().plusDays(90).toString());
        }
        if ("backup".equals(operation)) {
            return mapOf("backupId", randomId("BAK"), "backupSize", String.format("%.2f GB", 50 + (RANDOM.nextDouble() * 100)), "integrityCheck", "passed", "compressionRatio", String.format("%.2f", 0.6 + (RANDOM.nextDouble() * 0.3)), "estimatedRestoreTime", (30 + RANDOM.nextInt(60)) + " minutes", "storageLocation", "cloud".equals(stringValue(data.get("location"))) ? "cloud" : "local");
        }
        return mapOf("message", "operation processed");
    }

    private static List<ChartData> defaultCharts(String option, long now) {
        List<ChartData> charts = new ArrayList<ChartData>();
        ChartData.Builder builder = ChartData.newBuilder().setChartType("line").setTitle(option + " chart");
        for (int i = 0; i < 7; i++) {
            builder.addDataPoints(DataPoint.newBuilder().setLabel("P" + (i + 1)).setValue(100 + RANDOM.nextInt(500)).setUnit("count").setTimestamp(now - ((6 - i) * 86400)).build());
        }
        builder.putMetadata("period", "7d");
        charts.add(builder.build());
        return charts;
    }

    private static Map<String, Double> defaultSummaryMetrics(String option) {
        Map<String, Double> metrics = new LinkedHashMap<String, Double>();
        metrics.put(option + "_score", 80D + RANDOM.nextDouble() * 20D);
        metrics.put(option + "_growth", RANDOM.nextDouble() * 30D);
        metrics.put(option + "_coverage", 60D + RANDOM.nextDouble() * 35D);
        metrics.put(option + "_quality", 70D + RANDOM.nextDouble() * 25D);
        return metrics;
    }

    private static Map<String, Object> interceptorResult(String serviceName) {
        if ("user-service".equals(serviceName)) {
            return mapOf("status", "success", "user_count", 1000 + RANDOM.nextInt(10000), "active_users", 500 + RANDOM.nextInt(5000), "new_registrations", 10 + RANDOM.nextInt(100), "user_data", mapOf("total_users", 10000 + RANDOM.nextInt(50000), "premium_users", 1000 + RANDOM.nextInt(5000), "last_login_24h", 2000 + RANDOM.nextInt(8000)));
        }
        if ("order-service".equals(serviceName)) {
            return mapOf("status", "success", "total_orders", 1000 + RANDOM.nextInt(5000), "pending_orders", 50 + RANDOM.nextInt(200), "completed_orders", 800 + RANDOM.nextInt(4000), "order_data", mapOf("daily_orders", 100 + RANDOM.nextInt(500), "average_value", String.format("%.2f", 100 + RANDOM.nextDouble() * 500), "top_category", randomFrom(Arrays.asList("electronics", "clothing", "food", "books"))));
        }
        if ("payment-service".equals(serviceName)) {
            return mapOf("status", "success", "total_transactions", 2000 + RANDOM.nextInt(8000), "successful_payments", 1900 + RANDOM.nextInt(7500), "failed_payments", 10 + RANDOM.nextInt(100), "payment_data", mapOf("total_amount", String.format("%.2f", 100000 + RANDOM.nextDouble() * 1000000), "average_transaction", String.format("%.2f", 50 + RANDOM.nextDouble() * 200), "payment_methods", mapOf("credit_card", 30 + RANDOM.nextInt(40), "alipay", 25 + RANDOM.nextInt(30), "wechat_pay", 20 + RANDOM.nextInt(25))));
        }
        if ("inventory-service".equals(serviceName)) {
            return mapOf("status", "success", "total_products", 500 + RANDOM.nextInt(2000), "in_stock", 400 + RANDOM.nextInt(1800), "out_of_stock", 10 + RANDOM.nextInt(50), "inventory_data", mapOf("total_value", String.format("%.2f", 1000000 + RANDOM.nextDouble() * 5000000), "low_stock_alerts", 5 + RANDOM.nextInt(20), "categories", 20 + RANDOM.nextInt(50), "warehouses", 3 + RANDOM.nextInt(10)));
        }
        if ("analytics-service".equals(serviceName)) {
            return mapOf("status", "success", "reports_generated", 20 + RANDOM.nextInt(100), "data_points", 100000 + RANDOM.nextInt(1000000), "processing_time", String.format("%.2f", 1 + RANDOM.nextDouble() * 5), "analytics_data", mapOf("conversion_rate", String.format("%.2f", 5 + RANDOM.nextDouble() * 10), "bounce_rate", String.format("%.2f", 20 + RANDOM.nextDouble() * 30), "avg_session_duration", String.valueOf(120 + RANDOM.nextInt(300)), "top_pages", randomFrom(Arrays.asList("home", "product", "cart", "checkout"))));
        }
        return mapOf("status", "success", "messages_sent", 1000 + RANDOM.nextInt(5000), "delivery_rate", String.format("%.2f", 90 + RANDOM.nextDouble() * 10), "failed_deliveries", 5 + RANDOM.nextInt(50), "notification_data", mapOf("email_sent", 500 + RANDOM.nextInt(2000), "sms_sent", 200 + RANDOM.nextInt(1000), "push_sent", 800 + RANDOM.nextInt(3000), "channels", Arrays.asList("email", "sms", "push", "webhook")));
    }

    private static int expirySeconds(String expiry) {
        if ("24h".equals(expiry)) {
            return 86400;
        }
        if ("7d".equals(expiry)) {
            return 604800;
        }
        if ("30d".equals(expiry)) {
            return 2592000;
        }
        return 3600;
    }

    private static Map<String, Map<String, Object>> videoConfigs() {
        Map<String, Map<String, Object>> configs = new LinkedHashMap<String, Map<String, Object>>();
        configs.put("movie-action", mapOf("encryptionKey", "movie-action-key-2025", "segmentCount", 240));
        configs.put("series-drama", mapOf("encryptionKey", "series-drama-key-2025", "segmentCount", 90));
        configs.put("documentary", mapOf("encryptionKey", "documentary-key-2025", "segmentCount", 180));
        configs.put("live-stream", mapOf("encryptionKey", "live-stream-key-2025", "segmentCount", 20));
        return configs;
    }

    private static String generateVideoSegmentData(String videoType, int segmentIndex) {
        return MAPPERValue(mapOf(
            "header", "TS_PACKET_HEADER",
            "video_data", "VIDEO_SEGMENT_" + videoType.toUpperCase() + "_" + segmentIndex + "_" + UUID.randomUUID().toString().substring(0, 8),
            "audio_data", "AUDIO_" + segmentIndex,
            "metadata", mapOf("segment_index", segmentIndex, "timestamp", Instant.now().getEpochSecond(), "duration", 30, "video_codec", "H.264", "audio_codec", "AAC"),
            "footer", "TS_PACKET_FOOTER"
        ));
    }
}
