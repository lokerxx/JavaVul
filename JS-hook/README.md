# JS-hook

`JS-hook` 是集成在 `D:\JavaVul` 下的一套前端逆向与协议拆解训练场，当前以 `Spring Boot + 静态题页` 的形式运行。

## 入口

- 首页：`http://localhost:8080/`
- 题库总入口：`http://localhost:8080/js-labs.html`
- 后台页：`http://localhost:8080/admin.html`

## 当前覆盖范围

- `JS 逆向训练`：动态执行、字符串数组、控制流平坦化、反调试、JSFuck、动态签名、source map 缺失定位
- `协议与加解密`：AES-CBC、AES-ECB、AES-GCM、AES-RSA、RSA、DES、3DES、SM2、SM4、SM2+SM4、Query / Form / FormData、双向报文、头签名、动态密钥、重放窗口
- `XHR / Hook 实战`：query sign / encrypt、form body、JSON 字段加密、单字段加密、响应解密、header sign、cookie、Hex、Protobuf、拦截器链、视频分片

## 目前的判断

- 题型大类已经基本齐全
- 当前主要缺口不在“再加一个题型”，而在“把题库做成可运营靶场平台”

## 仍需继续补齐的能力

- 题目后台管理：题目增删改、难度、标签、答案开关、发布流程
- 统一判题：验证是否真实 Hook 到点、是否复现 sign、是否还原明文
- 用户与记录：登录、做题记录、提交历史、学习进度、排行榜
- 数据持久化：当前优先采用 SQLite，后续如有需要再补 Redis 支撑 nonce、防重放窗口与日志
- 微服务链路：Gateway、业务服务、上下游透传与真实业务流
- 前后端分离：独立学员端 / 管理端，而不只是静态 HTML 入口

## 关键目录

- `D:\JavaVul\JS-hook\src\main\java`：Spring Boot 启动类与后端接口
- `D:\JavaVul\JS-hook\src\main\resources\static`：首页、题库页与原始案例页
- `D:\JavaVul\JS-hook\src\main\resources\static\labs`：扩展题库与题目元数据
- `D:\JavaVul\JS-hook\public`：旧入口说明页

## 启动方式

```bash
cd D:\JavaVul\JS-hook
mvn spring-boot:run
```

## SQLite 持久化

- 默认数据库文件：`D:\JavaVul\JS-hook\js-hook.db`
- 初始化表：`lab_challenge`、`lab_submission`
- 启动时会自动把 `src/main/resources/static/labs/challenges.json` 同步到 SQLite

## 新增接口

- `GET /api/catalog/overview`：题库概览统计
- `GET /api/catalog/challenges`：按条件查询题目
- `GET /api/catalog/challenges/{id}`：查看单题元数据
- `POST /api/catalog/sync`：从 `challenges.json` 重新同步题库
- `POST /api/catalog/sync-rules`：同步默认判题规则
- `POST /api/catalog/submissions`：写入做题提交记录
- `POST /api/catalog/judge`：执行自动判题并写入提交记录
- `GET /api/catalog/submissions`：查看提交记录
- `GET /api/catalog/rules`：查看当前判题规则

## 建议的下一阶段路线

1. 先补 `题目后台 + 判题 + 提交记录`
2. 再补 `SQLite 持久化 + 可选 Redis`
3. 然后拆 `gateway + business-service`
4. 最后补 `学员端 / 管理端` 与容器化编排
