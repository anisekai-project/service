FROM gradle:8-alpine as build
LABEL authors="akio"

WORKDIR /source
ADD . .
RUN gradle clean build --no-daemon

FROM openjdk:17.0.2-slim as service
LABEL authors="akio"

RUN apt-get update && apt-get install -y wget xz-utils

RUN wget -O /usr/share/keyrings/gpg-pub-moritzbunkus.gpg https://mkvtoolnix.download/gpg-pub-moritzbunkus.gpg

RUN echo "deb [signed-by=/usr/share/keyrings/gpg-pub-moritzbunkus.gpg] https://mkvtoolnix.download/debian/ bullseye main" >> /etc/apt/sources.list.d/mkvtoolnix.list
RUN echo "deb-src [signed-by=/usr/share/keyrings/gpg-pub-moritzbunkus.gpg] https://mkvtoolnix.download/debian/ bullseye main" >> /etc/apt/sources.list.d/mkvtoolnix.list

RUN apt-get install -y mkvtoolnix && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN wget https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-amd64-static.tar.xz && \
    tar xvf 'ffmpeg-release-amd64-static.tar.xz' && \
    mv ffmpeg-6.1-amd64-static /var/opt/ffmpeg && \
    ln -s /var/opt/ffmpeg/ffmpeg /usr/bin/ffmpeg && \
    rm 'ffmpeg-release-amd64-static.tar.xz'

WORKDIR /app

COPY --from=build /source/build/libs/* ./
RUN rm -rf *-plain.jar && mv *.jar app.jar
ENTRYPOINT ["java", "-Djava.security.egdfile:/dev/./urandom", "-jar", "app.jar"]