FROM openjdk:11

COPY ./target/account-svc-1.0.0.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch account-svc-1.0.0.jar'

ENTRYPOINT ["java", "-jar", "account-svc-1.0.0.jar"]