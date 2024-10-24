package me.anisekai.modules.shizue.listeners;


import me.anisekai.api.persistence.IEntity;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.linn.services.data.AnimeDataService;
import me.anisekai.modules.shizue.entities.SeasonalVote;
import me.anisekai.modules.shizue.events.broadcast.BroadcastStatusUpdatedEvent;
import me.anisekai.modules.shizue.events.interest.InterestCreatedEvent;
import me.anisekai.modules.shizue.events.interest.InterestLevelUpdatedEvent;
import me.anisekai.modules.shizue.events.seasonalselection.SeasonalSelectionClosedUpdatedEvent;
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

        if (event.getEntity().isProgress()) {
            long animeId = event.getEntity().getAnime().getId();

            switch (event.getCurrent()) {
                case ACTIVE -> this.service.mod(animeId, this.service.tagWatching());
                case COMPLETED -> this.service.mod(
                        animeId,
                        this.service.progression(event.getEntity())
                );
            }
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
