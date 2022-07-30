FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /usr/src/app

COPY pom.xml pom.xml
COPY src src

RUN mvn clean package

FROM tomcat:10.0.23-jdk17 AS to-do-project

WORKDIR /usr/local/tomcat/

# Some changes to tomcat instalation
# Adds a setenv.sh script that calculates and adds CATALINA_HTTP_PORT to JAVA_OPTS on startup.
# Creating setenv.sh script is a method that is recommended for setting JAVA_OPTS in tomcat
COPY src/main/tomcat/setenv.sh bin/
# Replaces port=8080 with port=${port.http.nonssl} in server.xml
RUN sed -i "s/port=\"[0-9]\+\" protocol=\"HTTP\/1.1\"/port=\"\${port.http.nonssl}\" protocol=\"HTTP\/1.1\"/" conf/server.xml

COPY --from=build /usr/src/app/target/to-do-project.war webapps/ROOT.war

CMD ["catalina.sh", "run"]
