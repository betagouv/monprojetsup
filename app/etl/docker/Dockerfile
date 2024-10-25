# Use an official Maven image as the base image
FROM maven:3-eclipse-temurin-21-alpine AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and the project files to the container
COPY . .

# Build the application using Maven
RUN mvn clean package -DskipTests

# Rename jar file 
RUN mkdir -p /usr/src
RUN cp etl-updatedb/target/*-exec.jar /usr/src/mps-etl.jar

# Use an official OpenJDK image as the base image
FROM eclipse-temurin:21-jre-noble

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the previous stage to the container
COPY --from=build /usr/src/mps-etl.jar .

# Exposed port
EXPOSE 3000

RUN apt-get update && apt-get install -y \
  git \
  && rm -rf /var/lib/apt/lists/*

# Set the command to run the application
CMD ["java", "-jar", "mps-etl.jar"]
