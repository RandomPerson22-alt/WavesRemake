# ---- Build Stage ----
FROM gradle:8-jdk17 AS build
WORKDIR /app

# Copy everything and build
COPY . .
RUN gradle build --no-daemon

# ---- Run Stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port Render will use
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
