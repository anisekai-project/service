# ---- Build stage ----
FROM debian:12.1-slim AS build
ARG TRANSMISSION_VERSION="4.0.5"

RUN apt-get update \
    && apt-get install -y wget build-essential cmake libcurl4-openssl-dev libssl-dev python3 \
    && cd /tmp \
    && wget "https://github.com/transmission/transmission/releases/download/$TRANSMISSION_VERSION/transmission-$TRANSMISSION_VERSION.tar.xz" \
    && tar xf "transmission-$TRANSMISSION_VERSION.tar.xz" \
    && rm "transmission-$TRANSMISSION_VERSION.tar.xz" \
    && cd transmission-$TRANSMISSION_VERSION \
    && mkdir build \
    && cmake -DCMAKE_BUILD_TYPE=Release -S . -B ./build \
    && cd build \
    && make \
    && make install

# ---- Runtime stage ----
FROM debian:12.1-slim
LABEL authors="akio"

# runtime deps only
RUN apt-get update && apt-get install -y --no-install-recommends \
    libcurl4-openssl-dev libssl-dev python3 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /usr/local/bin/transmission-* /usr/local/bin/
COPY --from=build /usr/local/share/transmission /usr/local/share/transmission

ENTRYPOINT ["/usr/local/bin/transmission-daemon", "-f", \
  "-x", "/app/data/.pid", \
  "--log-level=debug", \
  "--logfile", "/app/data/transmission.log", \
  "-g", "/app/data"]
