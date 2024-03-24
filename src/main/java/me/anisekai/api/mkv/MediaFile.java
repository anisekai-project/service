package me.anisekai.api.mkv;

import me.anisekai.api.json.BookshelfJson;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class MediaFile {

    private final File            file;
    private final Set<MediaTrack> tracks;

    public MediaFile(File file, BookshelfJson json) {

        this.file   = file;
        this.tracks = new HashSet<>(json.readAll("tracks", MediaTrack::new));
    }

    public File getFile() {

        return this.file;
    }

    public Set<MediaTrack> getTracks() {

        return this.tracks;
    }

    public Set<MediaTrack> getTracks(MediaTrackType type) {

        return this.tracks.stream().filter(track -> track.getType() == type).collect(Collectors.toSet());
    }

}
