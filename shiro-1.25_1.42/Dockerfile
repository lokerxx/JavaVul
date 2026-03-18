FROM wushangleon/java:jdk8u112_maven as builder

COPY . /opt/shiro
WORKDIR /opt/shiro
RUN mvn package -DskipTests

FROM wushangleon/java:jdk8u112
COPY --from=builder /opt/shiro/target/shiro-1.25_1.42-1.0-SNAPSHOT.jar /opt/app.jar
CMD ["java", "-jar", "/opt/app.jar"]
