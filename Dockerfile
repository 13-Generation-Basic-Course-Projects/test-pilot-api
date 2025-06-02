# ---- Stage 1: Build ----
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy only pom.xml to leverage Docker cache for dependencies
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application (skip tests for faster builds; remove -DskipTests if you want tests to run)
RUN mvn clean package -DskipTests -B

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
