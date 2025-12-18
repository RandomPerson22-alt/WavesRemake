# ---- Build stage ----
FROM gradle:8.3-jdk17 AS builder

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build scripts first for caching
COPY server/gradlew .
COPY server/gradle/ gradle/
COPY server/build.gradle .
COPY server/settings.gradle .

# Copy source code
COPY server/src/ src/

# Make sure gradlew is executable
RUN chmod +x gradlew

# Build the fat JAR
RUN ./gradlew :server:fatJar --no-daemon

# ---- Runtime stage ----
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy fat JAR from builder stage
COPY --from=builder /app/build/libs/server.jar ./server.jar

# Expose server port (replace 8080 with your actual server port)
EXPOSE 8080

# Run the server
ENTRYPOINT ["java", "-jar", "server.jar"]
