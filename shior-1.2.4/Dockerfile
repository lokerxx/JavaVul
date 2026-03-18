FROM wushangleon/java:jdk8u112_maven as builder

COPY . /opt/shior
WORKDIR /opt/shior
RUN mvn package -DskipTests

FROM wushangleon/java:jdk8u112
COPY --from=builder /opt/shior/target/shior-1.2.4-1.0-SNAPSHOT.jar /opt/app.jar
CMD ["java", "-jar", "/opt/app.jar"]
