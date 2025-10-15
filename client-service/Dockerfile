# ---------- build ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src

# A) Install the proto jar first
COPY libs/crypto-proto-0.0.1-SNAPSHOT.jar /deps/crypto-proto.jar
RUN mvn -q org.apache.maven.plugins:maven-install-plugin:3.1.1:install-file \
    -Dfile=/deps/crypto-proto.jar \
    -DgroupId=com.example -DartifactId=crypto-proto \
    -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

# B) Parent POM (so <parent> resolves)
COPY pom.xml .

# C) Module POM + sources
COPY client-service/pom.xml client-service/pom.xml
COPY client-service/ client-service/

# D) Build + repackage
RUN mvn -q -DskipTests -f client-service/pom.xml clean package spring-boot:repackage

# E) Copy the bootable jar and verify manifest
RUN BOOT_JAR="$(find client-service/target -maxdepth 1 -type f -name '*.jar' ! -name '*original*' | head -n1)" \
 && jar -xf "$BOOT_JAR" META-INF/MANIFEST.MF \
 && grep -E '^(Main-Class|Start-Class):' META-INF/MANIFEST.MF \
 && rm -f META-INF/MANIFEST.MF \
 && cp "$BOOT_JAR" /app.jar

# ---------- runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app.jar /app/app.jar
ENV SERVER_PORT=8080
ENV SIDECAR_REST_BASE_URL=http://127.0.0.1:8081
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
