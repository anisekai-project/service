package me.anisekai.api.mkv;

import org.springframework.security.core.parameters.P;

import java.util.Arrays;
import java.util.List;

public enum MediaFormat {

    MPEG3("mp3", "MP3"),
    DIGITAL_THEATER_SYSTEM("dts", "DTS-HD Master Audio", "DTS", "AC-3", "E-AC-3"),
    ADVANCED_AUDIO_CODING("aac", "AAC"),
    FREE_LOSSLESS_AUDIO_CODEC("flac", "FLAC"),
    OGG("ogg", "Opus", "Vorbis"),
    WAVEFORM_AUDIO("wav", "PCM"),

    MPEG4("mp4", "AVC/H.264/MPEG-4p10", "HEVC/H.265/MPEG-H", "MPEG-4p2"),

    SUB_STATION_ALPHA("ssa", "SubStationAlpha"),
    SUB_RIP("srt", "SubRip/SRT"),
    WEB_VTT("vtt", "WebVTT");

    private final List<String> codecIds;
    private final String       extension;

    MediaFormat(String extension, String... codecs) {

        this.codecIds  = Arrays.asList(codecs);
        this.extension = extension;
    }

    public boolean canUseCodec(String str) {

        return this.codecIds.contains(str);
    }

    public String getExtension() {

        return this.extension;
    }

    public static MediaFormat fromCodec(String codec) {

        for (MediaFormat value : MediaFormat.values()) {
            if (value.canUseCodec(codec)) {
                return value;
            }
        }

        return null;
    }
}
