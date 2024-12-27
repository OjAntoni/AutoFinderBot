# Stage 1: Build the application
FROM gradle:8.12.0-jdk21 as build

# Set the working directory inside the container
WORKDIR /app

# Copy only the necessary files first (to leverage caching)
COPY build.gradle settings.gradle ./

# Pre-download dependencies
RUN gradle dependencies --no-daemon

# Copy the entire project
COPY src ./src

# Build the project
RUN gradle clean bootJar --no-daemon

# Stage 2: Package the application
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

