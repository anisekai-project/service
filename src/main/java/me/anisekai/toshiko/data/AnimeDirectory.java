package me.anisekai.toshiko.data;

import me.anisekai.toshiko.Texts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnimeDirectory {

    private final String               name;
    private final String               displayName;
    private final List<GroupDirectory> groups;
    private final List<EpisodeFile>    episodes;

    public AnimeDirectory(File directory) {

        this.name        = directory.getName();
        this.displayName = Texts.unslugify(directory.getName());

        this.groups   = new ArrayList<>();
        this.episodes = new ArrayList<>();

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile()) {
                this.episodes.add(new EpisodeFile(file, this));
            } else {
                this.groups.add(new GroupDirectory(file, this));
            }
        }
    }

    public String getName() {

        return this.name;
    }

    public String getDisplayName() {

        return this.displayName;
    }

    public List<GroupDirectory> getGroups() {

        return this.groups;
    }

    public List<EpisodeFile> getEpisodes() {

        return this.episodes;
    }
}
