FROM openjdk:8-jre-alpine

RUN apk add --no-cache bash

WORKDIR /usr/src/app

COPY gradle-wrapper.jar /usr/src/app

COPY . usr/src/app

CMD ["/usr/bin/java", "-jar", "gradlew"]
