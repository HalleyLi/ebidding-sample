FROM openjdk:11

COPY ./target/bwic-svc-1.0.0.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch bwic-svc-1.0.0.jar'

ENV JAVA_OPTS=""

ENTRYPOINT java $JAVA_OPTS -jar bwic-svc-1.0.0.jar