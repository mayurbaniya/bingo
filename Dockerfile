# ---- Build Stage ----
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (caching layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Run Stage ----
FROM openjdk:17
WORKDIR /app

# Expose your application port
EXPOSE 9090

# Copy the built JAR from build stage
COPY --from=build /app/target/bingo-0.0.1-SNAPSHOT.jar app.jar

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
