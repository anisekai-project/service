package me.anisekai.toshiko.listeners;


import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.SeasonalVote;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.events.animenight.AnimeNightFinishedEvent;
import me.anisekai.toshiko.events.animenight.AnimeNightStartedEvent;
import me.anisekai.toshiko.events.interest.InterestUpdatedEvent;
import me.anisekai.toshiko.events.selections.SeasonalSelectionClosedEvent;
import me.anisekai.toshiko.services.AnimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AnimeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnimeListener.class);

    private final AnimeService service;

    public AnimeListener(AnimeService service) {

        this.service = service;
    }

    @EventListener
    public void onAnimeNightStarted(AnimeNightStartedEvent event) {

        LOGGER.info(
                "onAnimeNightStarted: AnimeNight {} - ScheduledEvent {} - Anime {}",
                event.getAnimeNight().getId(),
                event.getScheduledEvent().getId(),
                event.getAnimeNight().getAnime().getId()
        );

        Anime anime = event.getAnimeNight().getAnime();
        if (anime.getStatus() == AnimeStatus.SIMULCAST || anime.getStatus() == AnimeStatus.WATCHING) {
            return;
        }

        this.service.setStatus(anime, switch (anime.getStatus()) {
            case CANCELLED, WATCHED, WATCHING, DOWNLOADED, DOWNLOADING, NOT_DOWNLOADED, NO_SOURCE, UNAVAILABLE -> AnimeStatus.WATCHING;
            case SIMULCAST, SIMULCAST_AVAILABLE -> AnimeStatus.SIMULCAST;
        });
    }

    @EventListener
    public void onAnimeNightFinished(AnimeNightFinishedEvent event) {

        LOGGER.info(
                "onAnimeNightFinished: AnimeNight {} - ScheduledEvent {} - Anime {}",
                event.getAnimeNight().getId(),
                event.getScheduledEvent().getId(),
                event.getAnimeNight().getAnime().getId()
        );

        Anime anime = event.getAnimeNight().getAnime();
        this.service.setProgression(anime, anime.getWatched() + event.getAnimeNight().getAmount());
    }

    @EventListener
    public void onSeasonalSelectionClosed(SeasonalSelectionClosedEvent event) {

        LOGGER.info(
                "onSeasonalSelectionClosed: SeasonalSelection {}",
                event.getSeasonalSelection().getId()
        );

        event.getSeasonalSelection()
             .getVotes()
             .stream()
             .map(SeasonalVote::getAnime)
             .distinct()
             .forEach(anime -> this.service.setStatus(anime, AnimeStatus.SIMULCAST));
    }

    @EventListener
    public void onInterestedChanged(InterestUpdatedEvent event) {

        Anime anime = event.getInterest().getAnime();
        this.service.announce(anime);
    }

}
