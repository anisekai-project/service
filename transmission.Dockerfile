FROM debian:12.1-slim
LABEL authors="akio"

ARG TRANSMISSION_VERSION="4.0.5"

RUN apt-get update \
    && apt-get install -y wget build-essential cmake libcurl4-openssl-dev libssl-dev python3 \
    && cd /tmp \
    && wget "https://github.com/transmission/transmission/releases/download/$TRANSMISSION_VERSION/transmission-$TRANSMISSION_VERSION.tar.xz" \
    && tar xf "transmission-$TRANSMISSION_VERSION.tar.xz" \
    && cd transmission-$TRANSMISSION_VERSION \
    && mkdir build \
    && cmake -DCMAKE_BUILD_TYPE=Release -S . -B ./build \
    && cd build \
    && make \
    && make install \
    && apt-get remove -y wget build-essential cmake \
    && apt-get autoremove -y \
    && rm -rf /var/lib/apt/lists/* \
    && rm -rf /tmp/* /var/tmp/*

WORKDIR /app

ENTRYPOINT ["/usr/local/bin/transmission-daemon", "-f", "-x", "/app/data/.pid", "--log-level=debug", "--logfile", "/app/data/transmission.log", "-g", "/app/data"]