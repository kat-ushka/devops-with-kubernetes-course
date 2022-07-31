FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /usr/src/app

COPY pom.xml pom.xml
COPY src src

RUN mvn clean package

FROM tomcat:10.0.23-jdk17 AS ping-pong
EXPOSE 8080
COPY --from=build /usr/src/app/target/pingpong.war /usr/local/tomcat/webapps/
CMD ["catalina.sh", "run"]
