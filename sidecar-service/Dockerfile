# syntax=docker/dockerfile:1

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY crypto-proto/pom.xml crypto-proto/
COPY sidecar-service/pom.xml sidecar-service/
COPY client-service/pom.xml client-service/
RUN mvn -pl sidecar-service -am dependency:go-offline

COPY crypto-proto/src crypto-proto/src
COPY sidecar-service/src sidecar-service/src
RUN mvn -pl sidecar-service -am package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/sidecar-service/target/sidecar-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081 9090
ENTRYPOINT ["java","-jar","/app/app.jar"]
