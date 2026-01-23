# Builder stage
FROM eclipse-temurin:17-jdk AS builder
WORKDIR application
ARG JAR_FILE=target/Spring-Boot-Portfolio-Project-0.0.1-SNAPSHOT.jar
COPY Spring-Boot-Portfolio-Project-0.0.1-SNAPSHOT.jar application.jar
RUN java -jar application.jar extract

#Final stage
FROM eclipse-temurin:17-jdk
WORKDIR application
COPY target/Spring-Boot-Portfolio-Project-0.0.1-SNAPSHOT.jar application.jar
ENTRYPOINT ["java","-jar", "application.jar"]
EXPOSE 8080