FROM debian:latest
LABEL authors="akio"
ARG FFMPEG_VERSION=7.0.1
WORKDIR /var/opt/ffmpeg

RUN apt-get update && \
    apt-get install -y wget && \
    mkdir -p /var/opt/ffmpeg/sources /var/opt/ffmpeg/binaries /var/opt/ffmpeg/build && \
    apt-get update && \
    apt-get install -y autoconf automake build-essential bzip2 cmake git-core libass-dev libfreetype6-dev \
                       libgnutls28-dev libmp3lame-dev libnuma-dev libopus-dev libsdl2-dev libtool libunistring-dev \
                       libva-dev libvdpau-dev libvorbis-dev libvpx-dev libx264-dev libx265-dev libxcb1-dev \
                       libxcb-shm0-dev libxcb-xfixes0-dev meson nasm ninja-build pkg-config texinfo wget yasm \
                       zlib1g-dev && \
    cd /var/opt/ffmpeg/sources && \
    git clone --depth 1 https://github.com/mstorsjo/fdk-aac && \
    cd fdk-aac && \
    autoreconf -fiv && \
    ./configure --prefix="/var/opt/ffmpeg/build" --disable-shared && \
    make && \
    make install && \
    cd /var/opt/ffmpeg/sources && \
    wget -O ffmpeg-$FFMPEG_VERSION.tar.bz2 https://ffmpeg.org/releases/ffmpeg-$FFMPEG_VERSION.tar.bz2 && \
    tar xjvf ffmpeg-$FFMPEG_VERSION.tar.bz2 && \
    rm ffmpeg-$FFMPEG_VERSION.tar.bz2 && \
    cd /var/opt/ffmpeg/sources/ffmpeg-$FFMPEG_VERSION && \
    PATH="/var/opt/ffmpeg/binaries:$PATH" PKG_CONFIG_PATH="/var/opt/ffmpeg/build/lib/pkgconfig" ./configure \
      --prefix="/var/opt/ffmpeg/build" \
      --pkg-config-flags="--static" \
      --extra-cflags="-I/var/opt/ffmpeg/build/include" \
      --extra-ldflags="-L/var/opt/ffmpeg/build/lib" \
      --extra-libs="-lpthread -lm" \
      --ld="g++" \
      --bindir="/var/opt/ffmpeg/binaries" \
      --enable-gpl --enable-gnutls \
      --enable-libass \
      --enable-libfdk-aac \
      --enable-libfreetype \
      --enable-libmp3lame \
      --enable-libopus \
      --enable-libvorbis \
      --enable-libvpx \
      --enable-libx264 \
      --enable-libx265 \
      --enable-nonfree && \
    PATH="/var/opt/ffmpeg/binaries:$PATH" make && \
    make install && \
    apt-get remove -y autoconf automake build-essential bzip2 cmake git-core libass-dev libfreetype6-dev \
                       libgnutls28-dev libmp3lame-dev libnuma-dev libopus-dev libsdl2-dev libtool libunistring-dev \
                       libva-dev libvdpau-dev libvorbis-dev libvpx-dev libx264-dev libx265-dev libxcb1-dev \
                       libxcb-shm0-dev libxcb-xfixes0-dev meson nasm ninja-build pkg-config texinfo wget yasm \
                       zlib1g-dev && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* /var/opt/ffmpeg/sources /var/opt/ffmpeg/build