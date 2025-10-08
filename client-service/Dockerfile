# -------- build stage --------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src

# 1) Copy POMs first for caching
COPY pom.xml .
COPY crypto-proto/pom.xml crypto-proto/pom.xml
COPY client-service/pom.xml client-service/pom.xml

# 2) Warm dependency cache; include crypto-proto via -am
RUN mvn -q -pl client-service -am dependency:go-offline

# 3) Copy sources and build
COPY crypto-proto/ crypto-proto/
COPY client-service/ client-service/
RUN mvn -q -DskipTests -pl client-service -am package

# -------- runtime stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app

# (Optional) run as non-root
RUN useradd -r -s /bin/false spring
COPY --from=build /src/client-service/target/*.jar /app/app.jar
RUN chown -R spring:spring /app
USER spring

# Port: HTTP (8080) — change if you need
EXPOSE 8080

# SERVER_PORT -> server.port (Spring relaxed binding)
ENV SERVER_PORT=8080

# Because both containers run in the same Pod, localhost:8081 hits sidecar
ENV SIDECAR_BASE_URL=http://127.0.0.1:8081

ENTRYPOINT ["java","-jar","/app/app.jar"]
