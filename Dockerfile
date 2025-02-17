# base image
FROM openjdk:17-jdk-slim

# set the working directory
WORKDIR /app

# copy the jar file
COPY build/libs/szs-0.0.1-SNAPSHOT.jar app.jar

# run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]