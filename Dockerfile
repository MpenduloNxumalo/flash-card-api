FROM maven:3.9.11-jdk-20 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:20-jre-slim
COPY --from=build /target/flash-card-api-0.0.1-SNAPSHOT.jar flash-card-api.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","flash-card-api.jar"]