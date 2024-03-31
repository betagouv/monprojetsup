# Start with a base image containing Java runtime (adopt OpenJDK 11 in this case)
FROM amazoncorretto:21-alpine-full

# Add Maintainer Info
LABEL maintainer="hugo.gimbert@beta.gouv.fr"

# Copy data into the container
COPY ./back/data/data  /data/back/data/data
COPY ./data /data/data
COPY ./back/docker/serverConfig.json /
COPY ./back/docker/suggestionsConfig.json /

# Make port 8002 available to the world outside this container
EXPOSE 8002

# The application's jar file
ARG JAR_FILE=back/java/target/monprojetsup-1.4.0-exec.jar

# Add the application's jar to the container
ADD ${JAR_FILE} monprojetsup.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/monprojetsup.jar"]
