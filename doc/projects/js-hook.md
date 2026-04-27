# JS-hook

## 位置

- 模块目录：`JS-hook`
- 默认端口：`48159`
- 首页入口：`/`
- 管理入口：`/admin.html`
- 题库入口：`/js-labs.html`

## 快速启动

推荐直接使用仓库根目录编排：

```bash
docker compose -f docker-compose-local.yaml up --build js-hook
```

如果只想单独运行这个模块：

```bash
cd JS-hook
mvn spring-boot:run
```

启动后可直接访问：

- `http://宿主机IP:48159/`
- `http://宿主机IP:48159/js-labs.html`
- `http://宿主机IP:48159/admin.html`

## 训练分组

- `JS 逆向训练`
  - 动态执行
  - 字符串数组恢复
  - 控制流平坦化
  - 反调试 / 高级反调试
  - JSFuck
  - 动态签名
  - source map 缺失定位
- `Galaxy 协议扩展`
  - AES-GCM Bidirectional
  - Dynamic Key Replay Window
  - AES-CBC Query Variant
  - AES-CBC Form Variant
  - AES-CBC Basic
  - AES-CBC Bidirectional
  - AES-ECB Basic
  - AES-GCM JSON Envelope
  - AES-RSA Hybrid Plus
  - RSA Basic Envelope
  - RSA Sign Header
  - Dynamic Key Session
  - DES-CBC Basic
  - 3DES-CBC Basic
  - AES-CBC FormData
  - SM2 Basic Envelope
  - SM2 Sign Header
  - SM2-SM4 Hybrid
  - SM4-CBC Basic
  - SM4-CBC Bidirectional
- `XHR Hook 原始案例`
  - 查询参数签名 / 加密
  - 表单与 JSON 字段加密
  - 响应字段解密与响应头 Cookie
  - Hex 双向加解密
  - Protobuf 请求 / 响应 / 双向通信
  - 拦截器与视频分片场景

## 当前实现

- 后端：`Spring Boot 2.6.6`
- 数据库：模块目录下的 `js-hook.db`
- 题库静态页：`src/main/resources/static/`
- 判题与持久化配置：`src/main/resources/judge-rules.json`、`src/main/resources/schema.sql`
- Proto 定义：`src/main/proto/`

当前容器和本地 `mvn spring-boot:run` 跑的都是同一套 Spring Boot JAR；仓库里保留的 `public/`、`server.js` 更适合作为历史示例或辅助素材参考，不是 compose 默认启动入口。

## 关键接口

- `GET /api/catalog/overview`：查看题库概览与统计
- `GET /api/catalog/challenges`：查询题目列表
- `GET /api/catalog/challenges/{id}`：查看单题元数据
- `POST /api/catalog/sync`：重新同步题库
- `POST /api/catalog/sync-rules`：同步判题规则
- `POST /api/catalog/submissions`：写入提交记录
- `POST /api/catalog/judge`：自动判题并落库
- `GET /api/catalog/submissions`：查看提交历史
- `GET /api/catalog/rules`：查看当前判题规则

除了题库接口，模块还保留了多组演示 API，比如 `/api/protobuf`、`/api/header-sign`、`/api/secure-submit`、`/api/video-segment/{videoType}/{segmentId}`，对应静态题页里的各类 Hook 与协议场景。

## 使用建议

1. 从 `js-labs.html` 统一进入，先做逆向题，再做协议扩展题。
2. 结合 `Network` 与 `Sources` 面板一起看，不要只盯页面源码。
3. 如果你要验证判题与持久化，建议同时打开 `admin.html` 和 `GET /api/catalog/overview`。
4. 如果要继续扩展，可以沿着“算法基线 → 传输形态 → 双向报文 → 混合加密 → 动态密钥 → 反调试 → 报文对抗”继续补题。
