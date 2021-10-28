FROM openjdk:8-jdk-alpine
RUN addgroup -S HR && adduser -S Employee -G HR
USER Employee:HR
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]