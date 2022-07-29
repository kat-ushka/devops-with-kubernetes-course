FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /usr/src/app

COPY pom.xml pom.xml
COPY log-output log-output
COPY ping-pong ping-pong

RUN mvn clean package -pl ping-pong -am

FROM tomcat:10.0.23-jdk17 AS ping-pong
EXPOSE 8080
COPY --from=build /usr/src/app/ping-pong/target/pingpong.war /usr/local/tomcat/webapps/
CMD ["catalina.sh", "run"]
