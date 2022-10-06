FROM maven:3.8.6-eclipse-temurin-8
RUN mkdir -p /app
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src
RUN ["mvn", "package"]

FROM resurfaceio/alpine-jdk17:3.16.2b
COPY --from=0 /app/target/*.jar ./
ENTRYPOINT ["java", "-jar"]
CMD ["logger-azure-1.1-SNAPSHOT.jar"]
