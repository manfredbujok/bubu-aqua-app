# Build Stage
FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn -B -ntp clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /build/target/*.jar /app/control-api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/control-api.jar"]