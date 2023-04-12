package me.anisekai.toshiko.helpers.fs;

import me.anisekai.toshiko.Texts;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class AnimeFs {

    private final UUID           uuid;
    private final File           path;
    private final String         name;
    private final Set<GroupFs>   groups;
    private final Set<EpisodeFs> files;

    public AnimeFs(File file) {

        this.uuid   = UUID.randomUUID();
        this.path   = file;
        this.name   = Texts.unslugify(file.getName());
        this.groups = new TreeSet<>(Comparator.comparing(GroupFs::getName));
        this.files  = new TreeSet<>(Comparator.comparing(EpisodeFs::getName));
    }

    public File getPath() {

        return this.path;
    }

    public String getName() {

        return this.name;
    }

    public void add(GroupFs groupFs) {

        this.groups.add(groupFs);
    }

    public void add(EpisodeFs episodeFs) {

        this.files.add(episodeFs);
    }

    public void finalize(CharSequence animeFsRoot, CharSequence subtitleFsRoot) {

        this.groups.forEach(g -> g.finalize(animeFsRoot, subtitleFsRoot));
        this.files.forEach(f -> f.finalize(animeFsRoot, subtitleFsRoot));
    }

    public JSONObject toJson() {

        JSONArray groups = new JSONArray();
        JSONArray files  = new JSONArray();

        this.groups.stream().map(GroupFs::toJson).forEach(groups::put);
        this.files.stream().map(EpisodeFs::toJson).forEach(files::put);

        JSONObject json = new JSONObject();
        json.put("id", this.uuid.toString());
        json.put("name", this.name);
        json.put("groups", groups);
        json.put("files", files);
        return json;
    }

}
