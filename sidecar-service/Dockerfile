# -------- build stage --------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src

# 1) Copy POMs first for better layer caching
COPY pom.xml .
COPY crypto-proto/pom.xml crypto-proto/pom.xml
COPY sidecar-service/pom.xml sidecar-service/pom.xml

# 2) Prime the dependency cache. -pl + -am ensures crypto-proto is included.
RUN mvn -q -pl sidecar-service -am dependency:go-offline

# 3) Copy sources and build only this module (and required deps)
COPY crypto-proto/ crypto-proto/
COPY sidecar-service/ sidecar-service/
RUN mvn -q -DskipTests -pl sidecar-service -am package

# -------- runtime stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app

# (Optional) run as non-root
RUN useradd -r -s /bin/false spring
COPY --from=build /src/sidecar-service/target/*.jar /app/app.jar
RUN chown -R spring:spring /app
USER spring

# Ports: HTTP (8081) + gRPC (9090) — change if you need
EXPOSE 8081 9090

# Spring Boot maps SERVER_PORT -> server.port automatically
ENV SERVER_PORT=8081

ENTRYPOINT ["java","-jar","/app/app.jar"]
