# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render 환경 변수를 사용)
EXPOSE 8081

# Command to run the application
CMD ["java", "-jar", "app.jar"]
