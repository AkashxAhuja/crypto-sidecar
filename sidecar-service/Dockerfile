FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src

# bring in POM + service sources
COPY pom.xml .
COPY sidecar-service/pom.xml sidecar-service/pom.xml
COPY sidecar-service/      sidecar-service/

# bring in the prebuilt proto jar and install it to /root/.m2
COPY libs/crypto-proto-0.0.1-SNAPSHOT.jar libs/
RUN mvn -q install:install-file \
  -Dfile=libs/crypto-proto-0.0.1-SNAPSHOT.jar \
  -DgroupId=com.example \
  -DartifactId=crypto-proto \
  -Dversion=0.0.1-SNAPSHOT \
  -Dpackaging=jar \
  -DgeneratePom=true

# build + repackage the service in one go (fat jar)
RUN mvn -q -DskipTests -f sidecar-service/pom.xml clean package spring-boot:repackage

# stash the bootable jar
RUN cp $(find sidecar-service/target -maxdepth 1 -type f -name '*.jar' ! -name '*original*' | head -n1) /app.jar

# ---- runtime ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app.jar /app/app.jar
ENV SERVER_PORT=8081
EXPOSE 8081 9090
ENTRYPOINT ["java","-jar","/app/app.jar"]