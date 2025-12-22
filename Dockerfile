# Stage 1: Build fatJar
FROM gradle:8.3-jdk17-alpine AS builder
WORKDIR /home/gradle/project
COPY . .
RUN gradle :server:fatJar --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Correct path: server module output
COPY --from=builder /home/gradle/project/server/build/libs/server.jar /app/server.jar

EXPOSE 54555
CMD ["java", "-jar", "server.jar"]
