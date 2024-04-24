FROM openjdk:11

RUN apt-get update && \ 
    apt-get install -y net-tools && \ 
    apt-get install -y jp2a && \
    apt-get install -y fim && \
    apt-get clean

    
WORKDIR /usr/src/app

RUN mkdir share

RUN apt install -y --no-install-recommends \
        tigervnc-standalone-server \
        tigervnc-common \
        dbus-x11 \
        novnc \
        net-tools \
 && cp /usr/share/novnc/vnc.html /usr/share/novnc/index.html

COPY ./share share
COPY *.class .
COPY *.java .

