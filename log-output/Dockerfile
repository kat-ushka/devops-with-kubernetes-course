FROM openjdk:17
EXPOSE 8080
WORKDIR /usr/src/app
ADD target/log-output-1.0-shaded.jar log-output.jar
CMD ["java","-jar","log-output.jar"]
