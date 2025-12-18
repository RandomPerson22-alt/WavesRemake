# Use a Java base image
FROM eclipse-temurin:17-jdk-alpine

# Copy the fatJar into the container
COPY build/libs/server-all.jar /app/server.jar
WORKDIR /app

# Expose the port the server listens on
EXPOSE 54555

# Run the server
CMD ["java", "-jar", "server.jar"]
