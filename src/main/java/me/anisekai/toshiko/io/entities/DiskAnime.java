package me.anisekai.toshiko.io.entities;

import me.anisekai.toshiko.Texts;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class DiskAnime {

    private final UUID             uuid;
    private final File             path;
    private final String           name;
    private final Set<DiskGroup>   groups;
    private final Set<DiskEpisode> files;

    public DiskAnime(File file) {

        this.uuid   = UUID.randomUUID();
        this.path   = file;
        this.name   = Texts.unslugify(file.getName());
        this.groups = new TreeSet<>(Comparator.comparing(DiskGroup::getName));
        this.files  = new TreeSet<>(Comparator.comparing(DiskEpisode::getName));
    }

    public UUID getUuid() {

        return this.uuid;
    }

    public File getPath() {

        return this.path;
    }

    public String getName() {

        return this.name;
    }

    public void add(DiskGroup diskGroup) {

        this.groups.add(diskGroup);
    }

    public void add(DiskEpisode diskEpisode) {

        this.files.add(diskEpisode);
    }

    public Set<DiskGroup> getGroups() {

        return this.groups;
    }

    public void finalize(CharSequence animeFsRoot, CharSequence subtitleFsRoot) {

        this.groups.forEach(g -> g.finalize(animeFsRoot, subtitleFsRoot));
        this.files.forEach(f -> f.finalize(animeFsRoot, subtitleFsRoot));
    }

    public JSONObject toJson() {

        JSONArray groups = new JSONArray();
        JSONArray files  = new JSONArray();

        this.groups.stream().map(DiskGroup::toJson).forEach(groups::put);
        this.files.stream().map(DiskEpisode::toJson).forEach(files::put);

        JSONObject json = new JSONObject();
        json.put("id", this.uuid.toString());
        json.put("name", this.name);
        json.put("groups", groups);
        json.put("files", files);
        return json;
    }

}
