# Stage 1: Build the fatJar
FROM gradle:8.3-jdk17-alpine AS builder
WORKDIR /home/gradle/project
COPY . .
RUN gradle :server:fatJar --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the fatJar from builder stage
COPY --from=builder /home/gradle/project/build/libs/server.jar /app/server.jar

# Expose the server port
EXPOSE 54555

# Run the server
CMD ["java", "-jar", "server.jar"]
