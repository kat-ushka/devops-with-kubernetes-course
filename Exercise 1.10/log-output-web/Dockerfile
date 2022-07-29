FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /usr/src/app

COPY pom.xml pom.xml
COPY log-output-web log-output-web
COPY timestamp-generator timestamp-generator

RUN mvn clean package -pl log-output-web -am

FROM tomcat:10.0.23-jdk17 AS log-output-web
COPY --from=build /usr/src/app/log-output-web/target/ROOT.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]
