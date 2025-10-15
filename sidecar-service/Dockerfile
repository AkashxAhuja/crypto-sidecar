# ---------- build ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src

# A) Install the proto jar into local repo (no parent POM in the image yet)
COPY libs/crypto-proto-0.0.1-SNAPSHOT.jar /deps/crypto-proto.jar
RUN mvn -q org.apache.maven.plugins:maven-install-plugin:3.1.1:install-file \
    -Dfile=/deps/crypto-proto.jar \
    -DgroupId=com.example -DartifactId=crypto-proto \
    -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

# B) Now provide the parent POM that the module references via <relativePath>../pom.xml</relativePath>
COPY pom.xml .

# C) Bring in the module POM + sources
COPY sidecar-service/pom.xml sidecar-service/pom.xml
COPY sidecar-service/ sidecar-service/

# D) Build + repackage in ONE invocation (produces a bootable fat-jar)
RUN mvn -q -DskipTests -f sidecar-service/pom.xml clean package spring-boot:repackage

# E) Copy the bootable jar (not the *.original) and FAIL FAST if it lacks Main/Start class
RUN BOOT_JAR="$(find sidecar-service/target -maxdepth 1 -type f -name '*.jar' ! -name '*original*' | head -n1)" \
 && jar -xf "$BOOT_JAR" META-INF/MANIFEST.MF \
 && grep -E '^(Main-Class|Start-Class):' META-INF/MANIFEST.MF \
 && rm -f META-INF/MANIFEST.MF \
 && cp "$BOOT_JAR" /app.jar

# ---------- runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app.jar /app/app.jar
ENV SERVER_PORT=8081
EXPOSE 8081 9090
ENTRYPOINT ["java","-jar","/app/app.jar"]
