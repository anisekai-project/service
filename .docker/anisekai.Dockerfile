FROM gradle:8.14.2-jdk21-alpine AS build

ARG namespace="anisekai-project"
ARG project="anisekai-service"
ARG branch="main"

WORKDIR /source
RUN git clone https://github.com/$namespace/$project.git && \
    cd $project &&  \
    git checkout $branch && \
    gradle clean build --no-daemon &&  \
    rm -rf /source/$project/build/libs/*-plain.jar &&  \
    mv /source/$project/build/libs/*.jar /app.jar &&  \
    rm -rf /source

FROM openjdk:21-bookworm AS service
LABEL authors="anisekai"

WORKDIR /app

# Install deps
RUN apt-get update &&  \
    apt-get install -y xz-utils &&  \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Installing ffmpeg
RUN curl https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-amd64-static.tar.xz -O && \
    tar xvf 'ffmpeg-release-amd64-static.tar.xz' && \
    mv ffmpeg-*-amd64-static /var/opt/ffmpeg && \
    ln -s /var/opt/ffmpeg/ffmpeg /usr/bin/ffmpeg && \
    ln -s /var/opt/ffmpeg/ffprobe /usr/bin/ffprobe && \
    rm 'ffmpeg-release-amd64-static.tar.xz'

COPY --from=build /app.jar .

# Cleanup
ENTRYPOINT ["java", "-Djava.security.egdfile:/dev/./urandom", "-jar", "app.jar"]
