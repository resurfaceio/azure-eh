FROM maven:3.9.9-eclipse-temurin-17
RUN mkdir -p /app
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src
RUN ["mvn", "package"]

FROM eclipse-temurin:17-jre
COPY --from=0 /app/target/*.jar ./
ENTRYPOINT ["java", "-jar"]
CMD ["logger-azure-1.2-SNAPSHOT.jar"]
