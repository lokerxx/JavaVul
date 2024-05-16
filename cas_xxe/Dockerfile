FROM wushangleon/java:jdk8u112_maven as builder

COPY . /opt/cas_xxe
WORKDIR /opt/cas_xxe
RUN  mvn  package  -DskipTests

FROM wushangleon/java:jdk8u112
# 复制构建好的 JAR 文件到容器
COPY --from=builder /opt/cas_xxe/target/cas_xxe-1.0-SNAPSHOT.jar /opt/app.jar

# 定义启动命令
CMD ["java", "-jar", "/opt/app.jar"]


