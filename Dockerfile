# Build stage using JDK 21
FROM openjdk:21-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Copy Gradle wrapper and project metadata
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Copy the source code
COPY src src

# Grant execute permission to Gradle wrapper
RUN chmod +x ./gradlew

# Build the project
RUN ./gradlew clean shadowJar

# Production stage using JDK 21
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=build /app/build/libs/community-bot-*.jar /app/bot.jar

# Expose ports if necessary (example: 8080)
EXPOSE 8080

# Set environment variables (if needed)
ENV BOT_ENV=production

# Run the bot
ENTRYPOINT ["java", "-jar", "/app/bot.jar"]
