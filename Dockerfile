FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install || true  # Continue even if tests fail
RUN mkdir -p /app/reports && cp -r /app/target/surefire-reports/* /app/reports/ || true
CMD ["ls", "/app/reports"]

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]