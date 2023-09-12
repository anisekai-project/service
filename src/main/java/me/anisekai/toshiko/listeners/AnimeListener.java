package me.anisekai.toshiko.listeners;


import me.anisekai.toshiko.entities.SeasonalVote;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.events.broadcast.BroadcastStatusUpdatedEvent;
import me.anisekai.toshiko.events.interest.InterestCreatedEvent;
import me.anisekai.toshiko.events.interest.InterestLevelUpdatedEvent;
import me.anisekai.toshiko.events.seasonalselection.SeasonalSelectionClosedUpdatedEvent;
import me.anisekai.toshiko.interfaces.persistence.IEntity;
import me.anisekai.toshiko.services.data.AnimeDataService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnimeListener {

    private final AnimeDataService service;

    public AnimeListener(AnimeDataService service) {

        this.service = service;
    }

    @EventListener
    public void onBroadcastStatusUpdated(BroadcastStatusUpdatedEvent event) {

        long animeId = event.getEntity().getAnime().getId();

        switch (event.getCurrent()) {
            case ACTIVE -> this.service.mod(animeId, this.service.tagWatching());
            case COMPLETED -> this.service.mod(animeId, this.service.progression(event.getEntity()));
        }
    }

    @EventListener
    public void onSeasonalSelectionClosedUpdated(SeasonalSelectionClosedUpdatedEvent event) {

        if (event.getCurrent()) {
            List<Long> votedAnimeIds = event.getEntity().getVotes().stream()
                                            .map(SeasonalVote::getAnime)
                                            .map(IEntity::getId)
                                            .toList();

            this.service.getProxy().batch(
                    repository -> repository.findAllById(votedAnimeIds),
                    animes -> {
                        animes.forEach(anime -> anime.setStatus(AnimeStatus.SIMULCAST));
                    }
            );
        }
    }

    @EventListener
    public void onInterestLevelUpdated(InterestLevelUpdatedEvent event) {

        this.service.announce(event.getEntity().getAnime());
    }

    @EventListener
    public void onInterestCreated(InterestCreatedEvent event) {

        this.service.announce(event.getEntity().getAnime());
    }

}
