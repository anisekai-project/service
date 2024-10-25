package me.anisekai.api.mkv;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public enum MediaTrackType {

    AUDIO(
            MediaFormat.MPEG3,
            MediaFormat.DIGITAL_THEATER_SYSTEM,
            MediaFormat.ADVANCED_AUDIO_CODING,
            MediaFormat.FREE_LOSSLESS_AUDIO_CODEC,
            MediaFormat.OGG,
            MediaFormat.WAVEFORM_AUDIO
    ),
    VIDEO(MediaFormat.MPEG4),
    SUBTITLES(MediaFormat.SUB_STATION_ALPHA, MediaFormat.SUB_RIP, MediaFormat.WEB_VTT);

    private final List<MediaFormat> formats;

    MediaTrackType(MediaFormat... formats) {

        this.formats = Arrays.asList(formats);
    }

    public List<MediaFormat> getFormats() {

        return this.formats;
    }

    public boolean is(File file) {

        return this.formats.stream().anyMatch(format -> file.getName().endsWith(format.getExtension()));
    }
}
