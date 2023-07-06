package me.anisekai.toshiko.io.video;

import me.anisekai.toshiko.io.DiskFile;

import java.io.File;
import java.util.stream.Stream;

public enum SubtitleCodec {

    SRT("S_TEXT/UTF8", "srt"),
    SSA("S_TEXT/ASS", "ssa"),
    USF("S_TEXT/USF", "usf"),
    SUB("S_VOBSUB", "sub"),
    SUP("S_HDMV/PGS", "sup"),
    VTT("WebVTT", "vtt");

    private final String codecId;
    private final String extension;

    SubtitleCodec(String codecId, String extension) {

        this.codecId   = codecId;
        this.extension = extension;
    }

    public static SubtitleCodec fromCodec(String codec) {

        for (SubtitleCodec value : SubtitleCodec.values()) {
            if (value.getCodecId().equals(codec)) {
                return value;
            }
        }

        throw new IllegalArgumentException("Unknown codec: " + codec);
    }

    public static boolean isSubtitle(String extension) {

        return Stream.of(SubtitleCodec.values()).map(SubtitleCodec::getExtension).anyMatch(extension::equalsIgnoreCase);
    }

    public static boolean isSubtitle(File file) {

        return isSubtitle(new DiskFile(file).getExtension());
    }

    public String getCodecId() {

        return this.codecId;
    }

    public String getExtension() {

        return this.extension;
    }
}
