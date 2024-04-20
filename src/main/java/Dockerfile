# Dockerfile for Java application
FROM openjdk:11

RUN apt-get update && \ 
    apt-get install -y net-tools && \ 
    apt-get clean

    
WORKDIR /usr/src/app

# Copy compiled Java classes to the container
COPY *.class .
COPY *.java .
COPY *.txt . 
