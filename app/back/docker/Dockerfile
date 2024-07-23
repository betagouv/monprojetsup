# Use an official Maven image as the base image
FROM maven:3-eclipse-temurin-17-alpine AS build

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
FROM eclipse-temurin:17-jre-ubi9-minimal

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the previous stage to the container
COPY --from=build /usr/src/mps-backend.jar .

# Exposed port
EXPOSE 3000

# Install postgresql client
RUN apt-get update && apt-get install -y \
  postgresql-client \
  && rm -rf /var/lib/apt/lists/*

# Set the command to run the application
CMD ["java", "-jar", "mps-backend.jar"]
