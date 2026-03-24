# JS-hook

## 位置

- 模块目录：`D:\JavaVul\JS-hook`
- 默认端口：`48159`
- 首页入口：`/`
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

## 使用建议

1. 从 `js-labs.html` 统一进入，先做逆向题，再做协议扩展题。
2. 结合 `Network` 与 `Sources` 面板一起看，不要只盯页面源码。
3. 如果要继续扩展，可以沿着“算法基线 → 传输形态 → 双向报文 → 混合加密 → 动态密钥 → 反调试 → 报文对抗”继续补题。
