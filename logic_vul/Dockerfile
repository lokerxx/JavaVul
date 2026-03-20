FROM wushangleon/java:jdk8u112_maven as builder

COPY . /opt/logic_vul
WORKDIR /opt/logic_vul
RUN mvn package -DskipTests

FROM wushangleon/java:jdk8u112
COPY --from=builder /opt/logic_vul/target/logic_vul-1.0-SNAPSHOT.jar /opt/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/opt/app.jar"]
