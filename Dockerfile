FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy the JAR file
COPY target/ticket-0.0.1-SNAPSHOT-jar-with-dependencies.jar app.jar

# Expose the port your application runs on
EXPOSE 8080

# Start the application with the correct main class
# Replace with your actual main class that has a main method
CMD ["java", "-cp", "app.jar", "com.hahnSoftware.ticket.TicketApplication"]