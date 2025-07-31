FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY mvnw.cmd .

# Check if mvnw exists, if not use Maven directly
COPY src src

# Build with Maven directly
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Final stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
