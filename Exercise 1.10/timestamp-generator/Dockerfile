FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /usr/src/app

COPY pom.xml pom.xml
COPY log-output-web log-output-web
COPY timestamp-generator timestamp-generator

RUN mvn clean package -pl timestamp-generator -am

FROM openjdk:17 AS timestamp-generator
WORKDIR /usr/src/app
COPY --from=build /usr/src/app/timestamp-generator/target/timestamp-generator-jar-with-dependencies.jar timestamp-generator.jar
EXPOSE 3000
CMD ["java","-jar","timestamp-generator.jar"]
