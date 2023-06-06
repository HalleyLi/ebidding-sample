FROM openjdk:11

COPY ./target/pricer-svc-1.0.0.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch pricer-svc-1.0.0.jar'

ENTRYPOINT ["java", "-jar", "pricer-svc-1.0.0.jar"]