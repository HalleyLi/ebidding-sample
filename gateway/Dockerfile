FROM openjdk:11

COPY ./target/gateway-svc-1.0.0.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch gateway-svc-1.0.0.jar'

ENTRYPOINT ["java", "-jar", "gateway-svc-1.0.0.jar"]