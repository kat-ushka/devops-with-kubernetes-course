FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /usr/src/app

COPY pom.xml pom.xml
COPY src src

RUN mvn clean package

FROM tomcat:10.0.23-jdk17 AS to-do-project

WORKDIR /usr/local/tomcat/

COPY src/main/tomcat/setenv.sh bin/
RUN sed -i "s/port=\"[0-9]\+\" protocol=\"HTTP\/1.1\"/port=\"\${port.http.nonssl}\" protocol=\"HTTP\/1.1\"/" conf/server.xml
COPY --from=build /usr/src/app/target/to-do-project.war webapps/ROOT.war

CMD ["catalina.sh", "run"]
