# Use an official OpenJDK image as a base
FROM eclipse-temurin:24-jdk-alpine
RUN apk update && apk add --no-cache curl
# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY ./PDF-Microservices-Worker-Management-Service/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application with remote debugging enabled
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]
