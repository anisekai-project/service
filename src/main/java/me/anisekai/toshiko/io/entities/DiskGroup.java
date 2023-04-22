package me.anisekai.toshiko.io.entities;

import me.anisekai.toshiko.Texts;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class DiskGroup {

    private final UUID             uuid;
    private final File             path;
    private final String           name;
    private final Set<DiskEpisode> files;

    public DiskGroup(File file) {

        this.uuid  = UUID.randomUUID();
        this.path  = file;
        this.name  = Texts.unslugify(file.getName());
        this.files = new TreeSet<>(Comparator.comparing(DiskEpisode::getName));
    }

    public File getPath() {

        return this.path;
    }

    public String getName() {

        return this.name;
    }

    public void finalize(CharSequence animeFsRoot, CharSequence subtitleFsRoot) {

        this.files.forEach(f -> f.finalize(animeFsRoot, subtitleFsRoot));
    }

    public void add(DiskEpisode diskEpisode) {

        this.files.add(diskEpisode);
    }

    public JSONObject toJson() {

        JSONArray episodes = new JSONArray();
        this.files.stream().map(DiskEpisode::toJson).forEach(episodes::put);

        JSONObject json = new JSONObject();
        json.put("id", this.uuid.toString());
        json.put("name", this.name);
        json.put("episodes", episodes);
        return json;
    }
}
