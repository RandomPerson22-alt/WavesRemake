# Use a JDK image for building and running
FROM openjdk:17-jdk-slim AS builder

# Set working directory
WORKDIR /app

# Copy only necessary files for Gradle caching
COPY server/build.gradle.kts server/settings.gradle.kts server/gradlew server/gradle /app/server/

# Copy the rest of the server source code
COPY server/src /app/server/src

# Make gradlew executable
RUN chmod +x /app/server/gradlew

# Build the fat JAR
WORKDIR /app/server
RUN ./gradlew :server:fatJar --no-daemon

# ---- Runtime stage ----
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the fat JAR from the builder stage
COPY --from=builder /app/server/build/libs/server.jar /app/server.jar

# Expose port (replace 8080 with your server port)
EXPOSE 8080

# Run the server
ENTRYPOINT ["java", "-jar", "/app/server.jar"]
