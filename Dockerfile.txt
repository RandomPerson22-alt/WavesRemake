# Use OpenJDK 17
FROM openjdk:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the server JAR
COPY build/libs/WavesRemake-server.jar /app/WavesRemake-server.jar

# Expose the port your server listens on
EXPOSE 54555

# Run the server
CMD ["java", "-jar", "WavesRemake-server.jar"]
