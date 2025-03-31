# Base image
FROM openjdk:17-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Copy all project files
COPY . .

# Grant execution permission to Gradle wrapper (if exists)
RUN chmod +x ./gradlew

# Build the application (creates the JAR file)
RUN ./gradlew clean bootJar

# -----------------------------

# Base image for running the app
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/szs-0.0.1-SNAPSHOT.jar app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
