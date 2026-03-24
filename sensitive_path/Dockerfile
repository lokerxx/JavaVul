FROM wushangleon/java:jdk8u112_maven as builder

COPY . /opt/sensitive_path
WORKDIR /opt/sensitive_path
RUN mvn package -DskipTests

FROM wushangleon/java:jdk8u112
COPY --from=builder /opt/sensitive_path/target/sensitive_path-1.0-SNAPSHOT.jar /opt/app.jar

EXPOSE 8080
CMD ["java", "-jar", "/opt/app.jar"]
