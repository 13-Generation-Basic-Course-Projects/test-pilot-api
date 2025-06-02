FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
# Download dependencies first for better layer caching if pom.xml hasn't changed
RUN mvn dependency:go-offline
COPY src ./src
# Build the application; let it fail if there are errors.
# Use -DskipTests if you don't want to run tests during Docker image build.
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app
# Replace 'your-artifact-id-version.jar' with the actual name of the JAR
# produced in the target folder by the Spring Boot Maven plugin.
# Check your /app/target/ directory in the builder stage if unsure.
COPY --from=builder /app/target/your-artifact-id-*.jar app.jar # Or the exact finalName.jar
# Ensure app.jar is correctly copied by listing contents (for debugging)
# RUN ls -l /app

EXPOSE 8080 # Make sure this matches your Spring Boot port (BACKEND_EXPOSE_PORT)
ENTRYPOINT ["java", "-jar", "app.jar"]