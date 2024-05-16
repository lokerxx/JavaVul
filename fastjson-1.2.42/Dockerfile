FROM wushangleon/java:jdk8u112_maven as builder

COPY . /opt/fastjson
WORKDIR /opt/fastjson
RUN  mvn  package  -DskipTests

FROM wushangleon/java:jdk8u112
# 复制构建好的 JAR 文件到容器
COPY --from=builder /opt/fastjson/target/fastjson-1.2.42-1.0-SNAPSHOT.jar /opt/app.jar

# 定义启动命令
CMD ["java", "-jar", "/opt/app.jar"]


