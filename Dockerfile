FROM maven:3.3.9-jdk-8
RUN mkdir -p /app
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src
#CMD mvn package exec:java
RUN ["mvn", "package"]

FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine-slim
COPY --from=0 /app/target/*.jar ./
RUN apk add --no-cache --upgrade wget less libcrypto1.1 libssl1.1 musl musl-utils
CMD ["java", "-jar", "logger-azure-1.0-SNAPSHOT-jar-with-dependencies.jar"]
