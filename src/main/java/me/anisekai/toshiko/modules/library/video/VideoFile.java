package me.anisekai.toshiko.modules.library.video;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class VideoFile {

    private final File             file;
    private final JSONObject       rawData;
    private final List<MediaTrack> tracks;

    public VideoFile(File file, JSONObject source) {

        this.file    = file;
        this.rawData = source;
        this.tracks  = new ArrayList<>();

        JSONArray tracks = source.getJSONArray("tracks");
        for (Object obj : tracks) {
            if (obj instanceof JSONObject json) {
                if (json.getString("type").equals("subtitles")) {
                    this.tracks.add(new MediaTrack(this, json));
                }
            }
        }
    }

    public List<MediaTrack> getTracks() {

        return this.tracks;
    }

    public File getFile() {

        return this.file;
    }

    @Override
    public String toString() {

        return this.rawData.toString(2);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        VideoFile videoFile = (VideoFile) o;
        return Objects.equals(this.getFile(), videoFile.getFile());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getFile());
    }

}
