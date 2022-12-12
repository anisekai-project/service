package me.anisekai.toshiko.data;

import me.anisekai.toshiko.Texts;

import java.io.File;

public class EpisodeFile {

    private final String name;
    private final String displayName;
    private final String uri;

    public EpisodeFile(File episode, AnimeDirectory anime) {

        this.name        = episode.getName();
        this.displayName = Texts.unslugifyExt(episode.getName());
        this.uri         = String.format("https://toshiko.alexpado.fr/animes/%s/%s", anime.getName(), episode.getName());
    }

    public EpisodeFile(File episode, GroupDirectory group) {
        this.name        = episode.getName();
        this.displayName = Texts.unslugifyExt(episode.getName());
        this.uri         = String.format("https://toshiko.alexpado.fr/animes/%s/%s/%s", group.getAnime().getName(), group.getName(), episode.getName());
    }

    public String getName() {

        return this.name;
    }

    public String getDisplayName() {

        return this.displayName;
    }

    public String getUri() {

        return this.uri;
    }
}
