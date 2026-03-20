# SimpleAgent 操作教程

- 类型：辅助项目
- 目录：`SimpleAgent`
- 端口：`无固定端口`
- 推荐入口：`agent/agent.jar`

## 这是什么

这是一个最小化的 Java Agent 示例，用来演示如何把自定义 Agent 挂到仓库里的各个靶场容器上。
它适合做下面几类事情：

1. 验证 `-javaagent` 是否成功加载。
2. 观察 Agent 是否能参与 IAST、链路追踪或字节码增强。
3. 快速替换为你自己的 Agent JAR，复用仓库现成的 compose 挂载方式。

## 具体操作步骤

1. 进入 `SimpleAgent` 目录，准备好 `src/main/java/my/agent/SimpleAgent.java`。
2. 创建 `MANIFEST.MF`，至少包含：

```text
Manifest-Version: 1.0
Premain-Class: my.agent.SimpleAgent
Can-Redefine-Classes: true
Can-Retransform-Classes: true
```

3. 编译 Agent 类：

```bash
javac -source 1.8 -target 1.8 -d . src/main/java/my/agent/SimpleAgent.java
```

4. 打包成可作为 Java Agent 使用的 JAR：

```bash
jar cvfm SimpleAgent.jar MANIFEST.MF my/agent/SimpleAgent.class
```

5. 把生成物放到仓库根目录的 `agent/agent.jar`：

```bash
mv SimpleAgent.jar ../agent/agent.jar
```

6. 重新执行 `bash run-local-build.sh`，让各靶场容器在启动时自动挂载这个 Agent。
7. 观察容器日志、应用启动输出，以及你自己的增强逻辑是否生效。

## 最小示例

```java
package my.agent;

import java.lang.instrument.Instrumentation;

public class SimpleAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("SimpleAgent 已加载");
    }
}
```

## 配合仓库使用

1. 当前根目录的 compose 文件已经默认挂载 `./agent/agent.jar`。
2. 如果你只是想验证 Agent 是否被加载，不需要改业务项目代码。
3. 如果你要测试被动扫描、IAST 或链路采集，可以直接把自己的 Agent JAR 替换进去。

## 相关入口

- 文档索引：[`doc/README.md`](../README.md)
- 根项目说明：[`README.md`](../../README.md)
