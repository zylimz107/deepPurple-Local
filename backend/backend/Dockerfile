FROM openjdk:23-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file to the container
COPY target/backend.jar app.jar

# Expose the port the app will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
