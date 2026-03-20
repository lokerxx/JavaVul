# JavaVul

![](https://socialify.git.ci/lokerxx/JavaVul/image?description=1&font=Inter&forks=1&name=1&owner=1&pattern=Circuit%20Board&stargazers=1&theme=Light)

## 介绍

Java 安全漏洞靶场集合，主要用于验证 IAST、被动扫描器和各类安全测试工具在真实业务接口场景下的效果。

仓库把不同漏洞拆成独立项目，并通过 Docker 为每个靶场提供隔离运行环境，方便按项目单独验证，也方便统一批量回放流量。

文章：[IAST实践总结](https://mp.weixin.qq.com/s/ahxKXv5eKcULVF_VqAjbyg)

## 快速开始

1. 克隆项目：

```sh
git clone https://github.com/lokerxx/JavaVul
cd JavaVul
```

2. 按需选择一种启动方式：

| 文件 | 作用 | 推荐命令 |
| :-- | :-- | :-- |
| `docker-compose-local.yaml` | 宿主机本地先构建，再启动全部靶场，构建速度更快，**推荐** | `bash run-local-build.sh` |
| `docker-compose-build.yaml` | 直接在容器内构建各项目，速度较慢 | `bash run-build_images.sh` |
| `docker-compose-remote.yaml` | 直接拉取我已发布的镜像，更新可能不及时 | `bash run-remote.sh` |

3. 启动后按需使用：

- 总控台：`http://宿主机IP:5000/`
- 单项目访问：参考 [`doc/project-tutorials.md`](./doc/project-tutorials.md)
- 接口批量回放：参考 [`doc/testing-pocs.md`](./doc/testing-pocs.md)

## 部署

下面的版本信息是我开发这个仓库时使用过的参考环境，不要求完全一致，但建议使用较新的 Docker 和 Docker Compose。

Maven 版本参考：

```sh
# mvn --version
Apache Maven 3.0.5 (Red Hat 3.0.5-17)
Maven home: /usr/share/maven
Java version: 1.8.0_192, vendor: Oracle Corporation
Java home: /usr/java/jdk1.8.0_192/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "3.10.0-1160.el7.x86_64", arch: "amd64", family: "unix"
```

Docker / Docker Compose 版本参考：

```sh
# docker version
Client:
 Version:         1.13.1
 API version:     1.26
 Package version: docker-1.13.1-209.git7d71120.el7.centos.x86_64
 Go version:      go1.10.3
 Git commit:      7d71120/1.13.1
 Built:           Wed Mar  2 15:25:43 2022
 OS/Arch:         linux/amd64

Server:
 Version:         1.13.1
 API version:     1.26 (minimum version 1.12)
 Package version: docker-1.13.1-209.git7d71120.el7.centos.x86_64
 Go version:      go1.10.3
 Git commit:      7d71120/1.13.1
 Built:           Wed Mar  2 15:25:43 2022
 OS/Arch:         linux/amd64
 Experimental:    false

# docker-compose version
docker-compose version 1.18.0, build 8dd22a9
docker-py version: 2.6.1
CPython version: 3.6.8
OpenSSL version: OpenSSL 1.0.2k-fips  26 Jan 2017
```

> 如果宿主机默认的 Docker / Docker Compose 版本过低，建议升级到更新版本后再运行。
>
> ```
>  yum remove docker \
>               docker-client \
>               docker-client-latest \
>               docker-common \
>               docker-latest \
>               docker-latest-logrotate \
>               docker-logrotate \
>               docker-engine
> 
> sudo yum install -y yum-utils
> sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
> 
> sudo yum install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin docker-compose
> ```

## 运行说明

- 运行前，请把 compose 里的 `flask.environment.HOST` 改成宿主机 IP，方便首页测试和回放脚本访问靶场。
- 当前 compose 默认已经挂载 `agent/agent.jar`。如果你要测试 IAST Agent，可以直接替换这个文件。
- `SimpleAgent` 的构建与挂载说明见 [`doc/projects/simpleagent.md`](./doc/projects/simpleagent.md)。
- 如果你要测试被动代理扫描，需要把 `index/app.py` 里的 `proxy_mode` 改成 `True`，并配置自己的代理地址 `proxies`。
- 仓库里的靶场较多，默认每个应用分配 `512M-1024M` 内存；全部启动时建议预留 `16G` 左右内存。
- 如果需要增大内存测试 Agent 或压力场景，可以统一调整 compose 文件里的 `-Xms512m -Xmx1024m`。

基础 Web 漏洞代码审计细节可参考：

- https://github.com/lokerxx/CybersecurityNote/tree/master/%E4%BB%A3%E7%A0%81%E5%AE%A1%E8%AE%A1/JAVA%E6%BC%8F%E6%B4%9E



## 支持靶场

当前仓库里的数据库类靶场已经统一为本地 SQLite 初始化，`base_vul`、`base_vul_repair`、`druid_unauthorized` 与 `druid_authorized` 都不再依赖 MySQL。
目前项目内已经移除了 MySQL 运行依赖，启动单体靶场时不需要额外准备数据库容器。

可以按下面几类理解当前仓库里的靶场：

### 基础 Web 漏洞与数据访问

| 文件夹 | 安全漏洞 | 测试用途 | 备注 |
| :-- | :-- | :-- | :-- |
| `base_vul` | SQL 注入、XSS、不安全文件操作、重定向、ReDoS、CRLF、命令执行、SPEL、SSRF、SSTI、不安全反射、XXE | 漏洞 | 综合基础漏洞集合 |
| `base_vul_repair` | 与 `base_vul` 对应的修复版本 | 修复 | 方便与漏洞版对照 |
| `HSQLDB` | HSQLDB 注入漏洞 | 修复、漏洞 |  |
| `Hibernate` | Hibernate 注入漏洞 | 修复、漏洞 |  |
| `druid_unauthorized` | Druid 未授权访问 | 漏洞 |  |
| `druid_authorized` | Druid 未授权访问修复版 | 修复 |  |
| `logic_vul` | 伪造身份、水平越权、垂直越权、流程绕过 | 漏洞 | 业务逻辑漏洞综合靶场 |

### Spring / Java 生态组件

| 文件夹 | 安全漏洞 | 测试用途 | 备注 |
| :-- | :-- | :-- | :-- |
| `actuator_unauthorized_1.X` | Actuator 未授权访问 1.X | 漏洞 |  |
| `actuator_authorized_1.X` | Actuator 未授权访问 1.X 修复版 | 修复 |  |
| `actuator_unauthorized_2.X` | Actuator 未授权访问 2.X | 漏洞 |  |
| `actuator_authorized_2.X` | Actuator 未授权访问 2.X 修复版 | 修复 |  |
| `log4jvul` | Log4j2 漏洞 | 漏洞 |  |
| `wxpay-xxe` | 微信支付 XXE | 漏洞 |  |
| `cas_xxe` | CAS XXE | 漏洞 | CAS 3.1.1-3.5.1 存在 XXE，修复版本为 3.6.0+ |

### 反序列化与表达式执行

| 文件夹 | 安全漏洞 | 测试用途 | 备注 |
| :-- | :-- | :-- | :-- |
| `fastjson-*` | 各版本 Fastjson 反序列化漏洞 | 漏洞 | 多版本并行维护 |
| `CVE-2019-10173` | XStream 反序列化漏洞 | 漏洞 |  |
| `CVE-2019-12384` | Jackson-databind 反序列化漏洞 | 漏洞 |  |
| `collections` | Commons Collections 反序列化 | 漏洞 | 已接入统一 compose 与回放脚本 |

### Shiro 系列

| 文件夹 | 安全漏洞 | 测试用途 | 备注 |
| :-- | :-- | :-- | :-- |
| `shior-1.2.4` | Apache Shiro 1.2.4 RememberMe 反序列化漏洞 | 漏洞 | `CVE-2016-4437` |
| `shiro-1.25_1.42` | Apache Shiro RememberMe Padding Oracle 靶场 | 漏洞 | `CVE-2019-12422` |
| `shiro-1.8.0` | Apache Shiro 1.8.0 弱 Key 集成配置靶场 | 漏洞 | 高版本仍使用公开弱 `rememberMe` key |
| `shiro-cve-2020-17523` | Apache Shiro 认证绕过靶场 | 漏洞 | `CVE-2020-17523` |

### Struts2 系列

| 文件夹 | 安全漏洞 | 测试用途 | 备注 |
| :-- | :-- | :-- | :-- |
| `struts2-s2-001` | Struts2 S2-001 OGNL 回填解析靶场 | 漏洞 | `CVE-2007-4556` |
| `struts2-s2-003` | Struts2 S2-003 参数名 OGNL 上下文污染靶场 | 漏洞 | `CVE-2008-6504` |
| `struts2-s2-005` | Struts2 S2-005 参数名 OGNL 命令执行靶场 | 漏洞 | `CVE-2010-1870` |
| `struts2-s2-007` | Struts2 S2-007 类型转换错误 OGNL 靶场 | 漏洞 | `CVE-2012-0838` |
| `struts2-s2-009` | Struts2 S2-009 参数二次求值 OGNL 靶场 | 漏洞 | `CVE-2011-3923` |
| `struts2-s2-012` | Struts2 S2-012 redirect 变量 OGNL 靶场 | 漏洞 | `CVE-2013-1965` |
| `struts2-s2-013` | Struts2 S2-013 includeParams OGNL 靶场 | 漏洞 | `CVE-2013-1966` |
| `struts2-s2-015` | Struts2 S2-015 通配符与二次引用 OGNL 靶场 | 漏洞 | `CVE-2013-2134` |






## 文档导航

- `SimpleAgent` 构建与挂载说明：[`doc/projects/simpleagent.md`](./doc/projects/simpleagent.md)
- 项目快速操作教程：[`doc/project-tutorials.md`](./doc/project-tutorials.md)
- 支持测试的接口清单与回放方式：[`doc/testing-pocs.md`](./doc/testing-pocs.md)
- 全部项目文档索引：[`doc/README.md`](./doc/README.md)

## 项目操作教程

每个项目的独立操作教程已经整理到 `doc/` 目录，建议优先从总索引进入：

[doc/README.md](./doc/README.md)

如果你想直接看“按项目怎么测”的总表入口，可以看：

[doc/project-tutorials.md](./doc/project-tutorials.md)


## 参考开发代码

- https://github.com/vulhub/vulhub
- https://github.com/l4yn3/micro_service_seclab
- https://github.com/ffffffff0x/JVWA
- https://github.com/mamba-2021/myjavavul
- https://github.com/zhlu32/range_java_micro_service_seclab
- https://rasp.baidu.com/doc/install/testcase.html
- https://github.com/lemono0/FastJsonParty/
- https://github.com/roottusk/vapi
- https://github.com/jweny/shiro-cve-2020-17523

## Star History Chart

[![Star History Chart](https://api.star-history.com/svg?repos=lokerxx/JavaVul&type=Date)](https://star-history.com/#lokerxx/JavaVul&Date)

## 待进行

- [x] cas-client xxe（漏洞和修复）
- [ ] SQL注入传 order by 参数, 白名单列表（误报）
