# Use an official Maven image as the base image
FROM maven:3.8.5-openjdk-17-slim AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and the project files to the container
COPY pom.xml .
COPY src ./src

# Build the application using Maven
RUN mvn clean package -DskipTests

# Rename jar file 
RUN cp target/*.?ar /usr/src/mps-backend.jar

# Use an official OpenJDK image as the base image
FROM openjdk:23-ea-17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the previous stage to the container
COPY --from=build /usr/src/mps-backend.jar .

# Exposed port
EXPOSE 3000

# Set the command to run the application
CMD ["java", "-jar", "mps-backend.jar"]
