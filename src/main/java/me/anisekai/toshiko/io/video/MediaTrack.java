package me.anisekai.toshiko.io.video;

import org.json.JSONObject;

import java.util.Objects;

public class MediaTrack {

    private final int           id;
    private final VideoFile     videoFile;
    private final SubtitleCodec codec;
    private final int           number;
    private final String        language;

    public MediaTrack(VideoFile videoFile, JSONObject trackData) {

        JSONObject properties = trackData.getJSONObject("properties");

        this.id        = trackData.getInt("id");
        this.videoFile = videoFile;
        this.codec     = SubtitleCodec.fromCodec(properties.getString("codec_id"));
        this.number    = properties.getInt("number");
        this.language  = properties.getString("language");
    }

    public int getId() {

        return this.id;
    }

    public SubtitleCodec getCodec() {

        return this.codec;
    }

    public int getNumber() {

        return this.number;
    }

    public String getLanguage() {

        return this.language;
    }

    public VideoFile getVideoFile() {

        return this.videoFile;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        MediaTrack track = (MediaTrack) o;
        return this.getId() == track.getId() && Objects.equals(this.videoFile, track.videoFile);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId(), this.videoFile);
    }
}
