# TESTPILOT API

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)

## Project Overview
TestPilot is an online platform developed to address the growing need for a dependable and convenient way for developers to test their APIs. APIs play an extremely crucial part in the development of applications, making it essential for developers to ensure their APIs work correctly and with the desired outcome. TestPilot makes it easy for developers to perform validation checks on their APIs, offering predefined validation pattern for quick and convenient testing, and custom test cases for greater flexibility and control. 

## Features
- User authentication and authorization using JWT
- Management of projects, collaborators, requests and test cases
- Email verification and password reset functionality
- Integration with GitHub for OAuth and user data
- RESTful APIs with input validation and global exception handling
- Caching and pagination support
- File upload and public share link generation
- Execution batch processing for test cases

## Tech Stack
- **Framework**: Spring Boot 3.4.4 (based on parent POM)
- **Language**: Java 21
- **Build Tool**: Maven
- **Database**: PostgreSQL (inferred from repository structure)
- **ORM**: MyBatis
- **Security**: Spring Security with JWT
- **API Documentation**: Springdoc OpenAPI (Swagger UI, inferred)
- **Utilities**: Lombok, MapStruct
- **Email**: Spring Mail
- **Caching**: Redis (inferred from potential usage)
- **Containerization**: Docker (via `docker-compose.yml`)

## Prerequisites
Ensure the following are installed before setting up the project:
- Java JDK 21
- Maven 3.8.x or higher
- PostgreSQL (recommended version: 15.x or higher)
- MinIO server (for file storage, if used)
- Redis server (for caching, if used)
- SMTP server (e.g., Gmail for email notifications)
- Docker (for containerized setup)
- IDE (e.g., IntelliJ IDEA, Eclipse, VS Code)

## Installation
1. **Clone the repository**:
   ```bash
   https://github.com/13-Generation-Basic-Course-Projects/test-pilot-api.git
   cd test-pilot-api
2. **Install dependencies**:
   ```bash
   mvn clean install
3. **Set up the database**:
   ```bash
   CREATE DATABASE test_pilot_db;
4. **Set up additional services (if used)**:
   ```bash
   Start MinIO and Redis servers locally or use remote instances.
   Configure SMTP for email notifications.

## Running the Application
1. **Run locally**:
   ```bash
   mvn spring-boot:run
2. **Run with Docker**:
   ```bash
   docker-compose up --build

## API Documentation
1. **To deploy the application**:
   ```bash
   http://localhost:8080/swagger-ui.html

## Configuration
  ```bash  
  spring.application.name=testing_pilot_backend
  
  minio.url=${MINIO_URL}
  minio.access.key=${MINIO_ACCESS_KEY}
  minio.access.secret=${MINIO_SECRET_KEY}
  minio.bucket.name=${MINIO_BUCKET_NAME}
  
  spring.datasource.driver-class-name=${DATASOURCE_DRIVER}
  spring.datasource.url=${DATASOURCE_URL}
  spring.datasource.username=${DATASOURCE_NAME}
  spring.datasource.password=${DATASOURCE_PASSWORD}
  
  app.dev.frontend.url=${FRONT_END_URL}
  
  spring.security.user.name=${SECURITY_USERNAME}
  spring.security.user.password=${SECURITY_PASSWORD}
  
  
  spring.devtools.restart.enabled=true
  spring.devtools.livereload.enabled=true
  
  
  spring.mail.host=${SPRING_MAIL_HOST}
  spring.mail.username=${SPRING_MAIL_USERNAME}
  spring.mail.password=${SPRING_MAIL_PASSWORD}
  spring.mail.support_email=${SPRING_MAIL_SUPPORT_EMAIL}
  spring.mail.properties.mail.transport.protocol=smtp
  spring.mail.properties.mail.smtp.port=587
  spring.mail.properties.mail.smtp.auth=true
  spring.mail.properties.mail.smtp.starttls.enable=true
  spring.mail.properties.mail.smtp.starttls.required=true
  spring.mail.properties.mail.smtp.ssl.trust=${SPRING_MAIL_HOST}
  
  management.endpoints.web.exposure.include=health,info,metrics
  
  otp.expiration=5
  
  google.client-id=${GOOGLE_CLIENT_ID}
  
  github.api=${GITHUB_API}
  github.client-id=${GITHUB_CLIENT_ID}
  github.client-secret=${GITHUB_CLIENT_SECRET}
  github.user-api=${GITHUB_USER_API}
  
  spring.rabbitmq.host=${RABBITMQ_HOST}
  spring.rabbitmq.port=${RABBITMQ_PORT}
  spring.rabbitmq.username=${RABBITMQ_USERNAME}
  spring.rabbitmq.password=${RABBITMQ_PASSWORD}
  spring.rabbitmq.listener.simple.auto-startup=true
  app.rabbitmq.execution-updates-queue=execution_updates_queue
  
  logging.level.com.both.testing_pilot_backend=DEBUG
  logging.level.org.springframework.amqp=DEBUG
  logging.level.org.springframework.web.reactive=DEBUG
