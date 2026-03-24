# fingerprint-playground

这个项目已经从“敏感目录靶场”改造成“指纹库训练场”，并且现在以 `src/main/resources/file/fingerprint-library.db` 作为主数据源。

## 当前数据源

- `src/main/resources/file/*.json`
  - 本地 JSON 指纹，共 `180` 条
  - 这些文件现在主要作为历史来源材料，运行时主读 SQLite
- `src/main/resources/import/cms.xls`
  - 来自 `r0eXpeR/fingerprint`
  - 共 `2088` 条 CMS 指纹
  - 匹配类型主要为 `md5` 与 `keyword`
- `src/main/resources/import/Dayu-Feature.json`
  - 来自 `r0eXpeR/fingerprint`
  - 共 `616` 条指纹
  - 识别类型映射为 `md5`、`keyword`、`header_keyword`

## 持久化

- SQLite 数据库：`src/main/resources/file/fingerprint-library.db`
- 表：`fingerprint_library`

## 页面与接口

- 首页：`/`
- 指纹目录：`GET /fingerprint/api/catalog`
- 指纹详情：`GET /fingerprint/api/records/{recordId}`
- 样本预览：`GET /fingerprint/api/sample/{recordId}`
- 指纹匹配：`POST /fingerprint/api/match`
- 重新导入：`POST /fingerprint/api/reload`
- 任意指纹路径回放：例如 `/images/admina/arrow.jpg`

## 运行方式

```bash
cd D:\JavaVul\sensitive_path
mvn spring-boot:run
```

## 重新构建 SQLite

```bash
E:\pytools\.venv311\Scripts\python.exe tools\build_fingerprint_db.py
```
