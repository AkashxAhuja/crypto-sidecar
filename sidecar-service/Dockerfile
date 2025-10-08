# ---------- build ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src

# 1) Copy POMs FIRST (cache-friendly) — include ALL module POMs
COPY pom.xml .
COPY crypto-proto/pom.xml crypto-proto/pom.xml
COPY sidecar-service/pom.xml sidecar-service/pom.xml
COPY client-service/pom.xml client-service/pom.xml   # <-- needed so aggregator sees the module

# 2) Prime dependency cache; -pl + -am ensures crypto-proto is included
RUN mvn -q -pl sidecar-service -am dependency:go-offline

# 3) Now copy sources for only the modules we actually build
COPY crypto-proto/ crypto-proto/
COPY sidecar-service/ sidecar-service/

# Build the service (and required deps)
RUN mvn -q -DskipTests -pl sidecar-service -am package

# ---------- runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd -r -s /bin/false spring
COPY --from=build /src/sidecar-service/target/*.jar /app/app.jar
RUN chown -R spring:spring /app
USER spring

# Expose HTTP and gRPC
EXPOSE 8081 9090
ENV SERVER_PORT=8081

ENTRYPOINT ["java","-jar","/app/app.jar"]
