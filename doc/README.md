# 项目文档索引

所有项目的独立操作教程都放在 `doc/projects/` 下面。现在每份文档都补充了更具体的测试步骤，默认会告诉你：

1. 先执行什么启动命令。
2. 从首页应该搜索什么关键词。
3. 直接访问哪个 URL 或复制哪条 `curl`。
4. 测试完成后应该重点观察什么现象。

当前仓库里的单体靶场已经统一改为项目内自初始化数据库，不再需要额外启动 MySQL。

## 使用建议

1. 单体靶场先运行 `bash run-local-build.sh`。
2. 需要统一发包时，打开 `http://宿主机IP:5000/`。
3. 需要做接口对照时，同时打开对应项目文档和首页模板。

## 文档列表

### 控制台与辅助项目

- [index](./projects/index.md)
- [SimpleAgent](./projects/simpleagent.md)
- [项目操作教程](./project-tutorials.md)
- [支持测试接口清单](./testing-pocs.md)

### 单体靶场

- [fastjson-1.2.24](./projects/fastjson-1-2-24.md)
- [fastjson-1.2.25-1.2.41](./projects/fastjson-1-2-25-1-2-41.md)
- [fastjson-1.2.42](./projects/fastjson-1-2-42.md)
- [fastjson-1.2.43](./projects/fastjson-1-2-43.md)
- [fastjson-1.2.45](./projects/fastjson-1-2-45.md)
- [fastjson-1.2.59](./projects/fastjson-1-2-59.md)
- [fastjson-1.2.60](./projects/fastjson-1-2-60.md)
- [fastjson-1.2.61](./projects/fastjson-1-2-61.md)
- [fastjson-1.2.62](./projects/fastjson-1-2-62.md)
- [fastjson-1.2.66](./projects/fastjson-1-2-66.md)
- [fastjson-1.2.67](./projects/fastjson-1-2-67.md)
- [fastjson-1.2.68](./projects/fastjson-1-2-68.md)
- [fastjson-1.2.80](./projects/fastjson-1-2-80.md)
- [fastjson-1.2.83](./projects/fastjson-1-2-83.md)
- [log4jvul](./projects/log4jvul.md)
- [druid_unauthorized](./projects/druid-unauthorized.md)
- [druid_authorized](./projects/druid-authorized.md)
- [actuator_unauthorized_2.X](./projects/actuator-unauthorized-2-x.md)
- [actuator_authorized_2.X](./projects/actuator-authorized-2-x.md)
- [actuator_unauthorized_1.X](./projects/actuator-unauthorized-1-x.md)
- [actuator_authorized_1.X](./projects/actuator-authorized-1-x.md)
- [base_vul](./projects/base-vul.md)
- [base_vul_repair](./projects/base-vul-repair.md)
- [HSQLDB](./projects/hsqldb.md)
- [Hibernate](./projects/hibernate.md)
- [wxpay-xxe](./projects/wxpay-xxe.md)
- [CVE-2019-10173](./projects/cve-2019-10173.md)
- [CVE-2019-12384](./projects/cve-2019-12384.md)
- [cas_xxe](./projects/cas-xxe.md)
- [shior-1.2.4](./projects/shior-1-2-4.md)
- [shiro-1.25_1.42](./projects/shiro-1-25-1-42.md)
- [shiro-1.8.0](./projects/shiro-1-8-0.md)
- [shiro-cve-2020-17523](./projects/shiro-cve-2020-17523.md)
- [struts2-s2-015](./projects/struts2-s2-015.md)
- [struts2-s2-013](./projects/struts2-s2-013.md)
- [struts2-s2-012](./projects/struts2-s2-012.md)
- [struts2-s2-009](./projects/struts2-s2-009.md)
- [struts2-s2-007](./projects/struts2-s2-007.md)
- [struts2-s2-005](./projects/struts2-s2-005.md)
- [struts2-s2-003](./projects/struts2-s2-003.md)
- [struts2-s2-001](./projects/struts2-s2-001.md)
- [collections](./projects/collections.md)
- [logic_vul](./projects/logic-vul.md)
