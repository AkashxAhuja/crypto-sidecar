# ---------- build ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src

# 1) Copy POMs FIRST (cache-friendly) — include ALL module POMs
COPY pom.xml .
COPY crypto-proto/pom.xml crypto-proto/pom.xml
COPY client-service/pom.xml client-service/pom.xml
COPY sidecar-service/pom.xml sidecar-service/pom.xml   # <-- include the sibling POM

# 2) Prime dependency cache; -pl + -am ensures crypto-proto is included
RUN mvn -q -pl client-service -am dependency:go-offline

# 3) Now copy sources for only the modules we actually build
COPY crypto-proto/ crypto-proto/
COPY client-service/ client-service/

# Build the service (and required deps)
RUN mvn -q -DskipTests -pl client-service -am package

# ---------- runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd -r -s /bin/false spring
COPY --from=build /src/client-service/target/*.jar /app/app.jar
RUN chown -R spring:spring /app
USER spring

EXPOSE 8080
ENV SERVER_PORT=8080
# same-pod call to sidecar
ENV SIDECAR_BASE_URL=http://127.0.0.1:8081

ENTRYPOINT ["java","-jar","/app/app.jar"]
