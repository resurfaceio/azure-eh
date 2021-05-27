FROM maven:3.3.9-jdk-8
RUN mkdir -p /app
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src
CMD mvn package exec:java