FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src

COPY pom.xml .
COPY client-service/pom.xml client-service/pom.xml
COPY client-service/      client-service/

COPY libs/crypto-proto-0.0.1-SNAPSHOT.jar libs/
RUN mvn -q install:install-file \
  -Dfile=libs/crypto-proto-0.0.1-SNAPSHOT.jar \
  -DgroupId=com.example \
  -DartifactId=crypto-proto \
  -Dversion=0.0.1-SNAPSHOT \
  -Dpackaging=jar \
  -DgeneratePom=true

RUN mvn -q -DskipTests -f client-service/pom.xml clean package spring-boot:repackage
RUN cp $(find client-service/target -maxdepth 1 -type f -name '*.jar' ! -name '*original*' | head -n1) /app.jar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app.jar /app/app.jar
ENV SERVER_PORT=8080
ENV SIDECAR_REST_BASE_URL=http://127.0.0.1:8081
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]