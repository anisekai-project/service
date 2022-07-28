package me.anisekai.toshiko.providers;

import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.PublicationState;
import me.anisekai.toshiko.interfaces.AnimeProvider;

import java.util.Optional;
import java.util.Set;

public class OfflineProvider implements AnimeProvider {

    private final String      name;
    private final String      link;
    private final AnimeStatus status;
    private final Integer     episode;

    public OfflineProvider(String name, String link, AnimeStatus status, Long episode) {

        this.name    = name;
        this.link    = link;
        this.status  = status;
        this.episode = Optional.ofNullable(episode).map(Long::intValue).orElse(null);
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

        return null;
    }

    @Override
    public PublicationState getPublicationState() {

        return this.status.getState();
    }

    @Override
    public Set<String> getTags() {

        return Set.of();
    }

    @Override
    public Optional<Integer> getEpisodeCount() {

        return Optional.ofNullable(this.episode);
    }

    @Override
    public Optional<Double> getRating() {

        return Optional.empty();
    }
}
