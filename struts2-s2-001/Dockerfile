FROM wushangleon/java:jdk8u112_maven as builder

COPY . /opt/struts2-s2-001
WORKDIR /opt/struts2-s2-001
RUN mvn package -DskipTests

FROM tomcat:7-jre8
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=builder /opt/struts2-s2-001/target/struts2-s2-001.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
