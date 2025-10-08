# ---------- build ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src

# Create module dirs and copy all POMs so the aggregator root POM parses cleanly
RUN mkdir -p crypto-proto sidecar-service client-service
COPY pom.xml .
COPY crypto-proto/pom.xml crypto-proto/pom.xml
COPY sidecar-service/pom.xml sidecar-service/pom.xml
COPY client-service/pom.xml client-service/pom.xml

# Warm the dependency cache for the targeted module (and its deps like crypto-proto)
RUN mvn -q -pl sidecar-service -am dependency:go-offline

# Copy only sources needed to build this image
COPY crypto-proto/ crypto-proto/
COPY sidecar-service/ sidecar-service/

# Build the service (and its required modules)
RUN mvn -q -DskipTests -pl sidecar-service -am package

# ---------- runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd -r -s /bin/false spring

# Copy the fat jar
COPY --from=build /src/sidecar-service/target/*.jar /app/app.jar

USER spring
EXPOSE 8081 9090
ENV SERVER_PORT=8081
ENTRYPOINT ["java","-jar","/app/app.jar"]