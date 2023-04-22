FROM openjdk:17.0.2-slim

RUN apt-get update && apt-get install -y ffmpeg mkvtoolnix && rm -rf /var/lib/apt/lists/*

WORKDIR /data
ADD build/libs .
RUN rm -rf *-plain.jar && mv *.jar app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
