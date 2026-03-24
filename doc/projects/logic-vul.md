# logic_vul 操作说明

- 类型：单体靶场
- 目录：`logic_vul`
- 端口：`8080`
- 推荐入口：`/logic-vul`

## 项目简介

`logic_vul` 是一个业务逻辑漏洞靶场，覆盖认证绕过、越权、流程绕过、验证码逻辑、短信轰炸、弱口令登录爆破，以及优惠券、退款、折扣、密码重置、审批流、状态机等典型场景。

## 页面入口

- 首页导航：`GET /logic-vul`
- 伪造身份：`GET /logic-vul/identity`
- 水平越权：`GET /logic-vul/horizontal`
- 垂直越权：`GET /logic-vul/vertical`
- 流程绕过：`GET /logic-vul/checkout`
- 短信验证码逻辑：`GET /logic-vul/sms-code`
- 短信轰炸：`GET /logic-vul/sms-bomb`
- 弱口令登录爆破：`GET /auth/bruteforce-vul`
- 图形验证码登录：`GET /auth/bruteforce-safe`
- 优惠券重复核销：`GET /logic-vul/coupon`
- 重复退款：`GET /logic-vul/refund`
- 折扣叠加：`GET /logic-vul/discount`
- 密码重置 Token 复用：`GET /logic-vul/reset`
- 审批流跳步：`GET /logic-vul/approval`
- 订单状态机绕过：`GET /logic-vul/state-machine`

## 调试接口

- 场景总览：`GET /logic-vul/info`
- 漏洞版登录：`POST /auth/login-vul`
- 安全版登录：`POST /auth/login-safe`
- 当前登录用户：`GET /auth/me`

## 业务逻辑漏洞接口

- 水平越权：`GET /api/personal/{profileId}/vul`、`GET /api/personal/{profileId}/safe`
- 垂直越权：`GET /api/admin/report/vul`、`GET /api/admin/report/safe`
- 流程绕过：`POST /api/orders/{orderId}/checkout/vul`、`POST /api/orders/{orderId}/checkout/safe`
- 短信验证码逻辑：`POST /sms/send-vul`、`POST /sms/verify-vul`、`POST /sms/send-safe`、`POST /sms/verify-safe`
- 短信轰炸：`POST /sms/bomb-vul`、`POST /sms/bomb-safe`
- 优惠券重复核销：`POST /promo/coupons/redeem/vul`、`POST /promo/coupons/redeem/safe`
- 重复退款：`POST /payments/{orderId}/refund/vul`、`POST /payments/{orderId}/refund/safe`
- 折扣叠加：`POST /pricing/discounts/calculate/vul`、`POST /pricing/discounts/calculate/safe`
- 密码重置 Token 复用：`POST /auth/reset/send-vul`、`POST /auth/reset/confirm-vul`、`POST /auth/reset/send-safe`、`POST /auth/reset/confirm-safe`
- 审批流跳步：`POST /workflow/approval/{taskId}/vul`、`POST /workflow/approval/{taskId}/safe`
- 订单状态机绕过：`POST /workflow/orders/{orderId}/state/vul`、`POST /workflow/orders/{orderId}/state/safe`

## 登录爆破与验证码场景

- 漏洞版提示接口：`GET /auth/bruteforce-vul/hints`
- 漏洞版登录提交：`POST /auth/bruteforce-vul/login`
- 安全版验证码初始化：`GET /auth/bruteforce-safe/captcha/new`
- 安全版验证码图片：`GET /auth/bruteforce-safe/captcha/image?token=...`
- 安全版登录提交：`POST /auth/bruteforce-safe/login`

### 说明

1. 漏洞版登录依赖 SQLite 表 `brute_force_users`，预置了多组弱口令账号。
2. 漏洞版会区分“用户名不存在”和“密码错误”，并且没有验证码、锁定和冷却机制。
3. 安全版要求输入数字字母混合的图形验证码，验证码单次有效，约 2 分钟过期。
4. 安全版统一返回“用户名、密码或验证码错误”，连续失败 5 次后锁定 60 秒。

## 推荐验证方式

1. 先打开 `GET /logic-vul`，逐个点击进入对应页面。
2. 每个页面都提供漏洞版与安全版请求示例，可直接点击执行。
3. 需要 token 的页面，先点击“获取安全版 token”后再执行安全版接口。
4. 如需查看所有种子数据和场景清单，访问 `GET /logic-vul/info`。
