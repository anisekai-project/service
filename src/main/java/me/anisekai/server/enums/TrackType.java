package me.anisekai.server.enums;

import me.anisekai.api.mkv.MediaTrackType;

public enum TrackType {
    AUDIO(".mp3"),
    VIDEO(".mp4"),
    SUBTITLE(".ass");

    private final String extension;

    TrackType(String extension) {

        this.extension = extension;
    }

    public static TrackType ofMediaTrackType(MediaTrackType type) {

        return switch (type) {
            case AUDIO -> AUDIO;
            case VIDEO -> VIDEO;
            case SUBTITLES -> SUBTITLE;
        };
    }

    public String getExtension() {

        return this.extension;
    }
}
