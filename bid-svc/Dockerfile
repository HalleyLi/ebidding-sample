FROM openjdk:11

COPY ./target/bid-svc-1.0.0.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch bid-svc-1.0.0.jar'

ENTRYPOINT ["java", "-jar", "bid-svc-1.0.0.jar"]