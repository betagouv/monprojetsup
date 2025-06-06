# Use an official Maven image as the base image
FROM maven:3-eclipse-temurin-21-alpine AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and the project files to the container
COPY ./suggestions ./suggestions
COPY ./etl ./etl

# Build the application using Maven
RUN cd ./etl && mvn clean compile install -DskipTests=true
RUN cd ./suggestions && mvn clean package -DskipTests

# Rename jar file 
RUN mkdir -p /usr/src
RUN cp ./suggestions/suggestions-server/target/*.jar /usr/src/mps-suggestions.jar

# Use an official OpenJDK image as the base image
FROM eclipse-temurin:21-jre-noble

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the previous stage to the container
COPY --from=build /usr/src/mps-suggestions.jar .

# Exposed port
EXPOSE 3000

# Set the command to run the application
CMD ["java", "-jar", "mps-suggestions.jar"]
