FROM gradle:8.14.2-jdk21-alpine AS anisekai
WORKDIR /source
RUN git clone https://github.com/anisekai-project/anisekai-service.git && \
    cd anisekai-service &&  \
    gradle clean build --no-daemon &&  \
    rm -rf /source/anisekai-service/build/libs/*-plain.jar &&  \
    mv /source/anisekai-service/build/libs/*.jar /app.jar &&  \
    rm -rf /source

FROM openjdk:21-bookworm AS service
LABEL authors="akio"
WORKDIR /app

# Install deps
RUN apt-get update &&  \
    apt-get install -y xz-utils &&  \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Installing ffmpeg
RUN curl https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-amd64-static.tar.xz -O && \
    tar xvf 'ffmpeg-release-amd64-static.tar.xz' && \
    mv ffmpeg-7.0.2-amd64-static /var/opt/ffmpeg && \
    ln -s /var/opt/ffmpeg/ffmpeg /usr/bin/ffmpeg && \
    ln -s /var/opt/ffmpeg/ffprobe /usr/bin/ffprobe && \
    rm 'ffmpeg-release-amd64-static.tar.xz'

COPY --from=anisekai /app.jar .

# Cleanup
ENTRYPOINT ["java", "-Djava.security.egdfile:/dev/./urandom", "-jar", "app.jar"]
