package me.anisekai.toshiko.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.anisekai.toshiko.Texts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupDirectory {

    private final AnimeDirectory    anime;
    private final String            name;
    private final String            displayName;
    private final List<EpisodeFile> episodes;

    public GroupDirectory(File directory, AnimeDirectory anime) {

        this.anime       = anime;
        this.name        = directory.getName();
        this.displayName = Texts.unslugify(directory.getName());

        this.episodes = new ArrayList<>();

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile()) {
                this.episodes.add(new EpisodeFile(file, this));
            }
        }
    }

    @JsonIgnore
    public AnimeDirectory getAnime() {

        return this.anime;
    }

    public String getName() {

        return this.name;
    }

    public String getDisplayName() {

        return this.displayName;
    }

    public List<EpisodeFile> getEpisodes() {

        return this.episodes;
    }
}
