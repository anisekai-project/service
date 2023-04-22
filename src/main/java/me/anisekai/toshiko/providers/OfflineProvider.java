package me.anisekai.toshiko.providers;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.PublicationState;
import me.anisekai.toshiko.interfaces.AnimeProvider;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class OfflineProvider implements AnimeProvider {

    private final String      name;
    private final String      image;
    private final String      link;
    private final AnimeStatus status;
    private final Long        episode;

    public OfflineProvider(Anime anime) {

        this.name    = anime.getName();
        this.image   = anime.getImage();
        this.link    = anime.getLink();
        this.status  = anime.getStatus();
        this.episode = anime.getTotal();
    }

    public OfflineProvider(String name, String image, String link, AnimeStatus status, Long episode) {

        this.name    = name;
        this.image   = image;
        this.link    = link;
        this.status  = status;
        this.episode = episode;
    }

    @Override
    public String getName() {

        return this.name;
    }

    @Override
    public String getSynopsis() {

        return null;
    }

    @Override
    public String getUrl() {

        return this.link;
    }

    @Override
    public String getImage() {

        return this.image;
    }

    @Override
    public PublicationState getPublicationState() {

        return this.status.getState();
    }

    @Override
    public Set<String> getGenres() {

        return Collections.emptySet();
    }

    @Override
    public Set<String> getThemes() {

        return Collections.emptySet();
    }

    @Override
    public Optional<Long> getEpisodeCount() {

        return Optional.ofNullable(this.episode)
                       .map(ep -> ep <= 0 ? null : ep);
    }

    @Override
    public Optional<Double> getRating() {

        return Optional.empty();
    }
}
