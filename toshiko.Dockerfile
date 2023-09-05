FROM gradle:8-alpine as build
LABEL authors="akio"

WORKDIR /source
ADD . .
RUN gradle clean build

FROM openjdk:17.0.2-slim as service
LABEL authors="akio"

RUN apt-get update  \
    && apt-get install -y ffmpeg mkvtoolnix  \
    && rm -rf /var/lib/apt/lists/* /tmp /var/tmp

WORKDIR /app
COPY --from=build /source/build/libs/* ./
RUN rm -rf *-plain.jar && mv *.jar app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]