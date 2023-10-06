FROM gradle:8-alpine as build
LABEL authors="akio"

WORKDIR /source
ADD . .
RUN gradle clean build

FROM debian:12.1-slim as service
LABEL authors="akio"

RUN apt-get update && apt-get install -y wget openjdk-17-jre xz-utils && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
RUN wget -O /usr/bin/mkvmerge https://mkvtoolnix.download/appimage/MKVToolNix_GUI-79.0-x86_64.AppImage && chmod +x /usr/bin/mkvmerge

RUN wget https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-amd64-static.tar.xz && \
    tar xvf 'ffmpeg-release-amd64-static.tar.xz' && \
    mv ffmpeg-6.0-amd64-static /var/opt/ffmpeg && \
    ln -s /var/opt/ffmpeg/ffmpeg /usr/bin/ffmpeg && \
    rm 'ffmpeg-release-amd64-static.tar.xz'

WORKDIR /app

COPY --from=build /source/build/libs/* ./
RUN rm -rf *-plain.jar && mv *.jar app.jar
ENTRYPOINT ["java", "-Djava.security.egdfile:/dev/./urandom", "-jar", "app.jar"]