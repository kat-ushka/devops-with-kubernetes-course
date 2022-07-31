FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /usr/src/app

COPY pom.xml pom.xml
COPY src src

RUN mvn clean package

FROM openjdk:17 AS timestamp-generator
WORKDIR /usr/src/app
COPY --from=build /usr/src/app/target/timestamp-generator-jar-with-dependencies.jar timestamp-generator.jar
EXPOSE 3000
CMD ["java","-jar","timestamp-generator.jar"]
