FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /usr/src/app

COPY pom.xml pom.xml
COPY src src

RUN mvn clean package

FROM tomcat:10.0.23-jdk17 AS log-output
COPY --from=build /usr/src/app/target/ROOT.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]
