package me.anisekai.listeners;

import me.anisekai.discord.tasks.anime.announcement.AnnouncementFactory;
import me.anisekai.discord.tasks.broadcast.schedule.BroadcastScheduleFactory;
import me.anisekai.discord.tasks.watchlist.update.WatchlistUpdateFactory;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.Broadcast;
import me.anisekai.server.entities.Voter;
import me.anisekai.server.enums.AnimeStatus;
import me.anisekai.server.events.AnimeCreatedEvent;
import me.anisekai.server.events.AnimeUpdatedEvent;
import me.anisekai.server.events.BroadcastCreatedEvent;
import me.anisekai.server.events.InterestCreatedEvent;
import me.anisekai.server.events.anime.*;
import me.anisekai.server.events.broadcast.BroadcastStatusUpdatedEvent;
import me.anisekai.server.events.discorduser.DiscordUserEmoteUpdatedEvent;
import me.anisekai.server.events.interest.InterestLevelUpdatedEvent;
import me.anisekai.server.events.selection.SelectionStatusUpdatedEvent;
import me.anisekai.server.interfaces.IAnime;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.TaskService;
import me.anisekai.server.services.VoterService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class responsible to listen for events from one entity that should impact another entity or impact discord.
 */
@Component
public class EventListeners {

    private final TaskService  taskService;
    private final AnimeService animeService;
    private final VoterService voterService;

    public EventListeners(TaskService taskService, AnimeService animeService, VoterService voterService) {

        this.taskService  = taskService;
        this.animeService = animeService;
        this.voterService = voterService;
    }

    // <editor-fold desc="Anime">

    @EventListener
    public void onAnimeCreated(AnimeCreatedEvent event) {

        this.taskService.getFactory(AnnouncementFactory.class).queue(event.getEntity());

        if (event.getEntity().getWatchlist().shouldDisplayList()) {
            this.taskService.getFactory(WatchlistUpdateFactory.class).queue(event.getEntity().getWatchlist());
        }
    }

    @EventListener({
            AnimeAddedByUpdatedEvent.class,
            AnimeNautiljonUrlUpdatedEvent.class,
            AnimeSynopsisUpdatedEvent.class,
            AnimeTagsUpdatedEvent.class,
            AnimeThumbnailUpdatedEvent.class,
            AnimeTitleUpdatedEvent.class,
            AnimeTotalUpdatedEvent.class,
    })
    public void onAnimeGenericUpdated(AnimeUpdatedEvent<?> event) {

        this.taskService.getFactory(AnnouncementFactory.class).queue(event.getEntity());
    }

    @EventListener({
            AnimeNautiljonUrlUpdatedEvent.class,
            AnimeTitleUpdatedEvent.class
    })
    public void onAnimeDataUpdated(AnimeUpdatedEvent<?> event) {

        if (event.getEntity().getWatchlist().shouldDisplayList()) {
            this.taskService.getFactory(WatchlistUpdateFactory.class).queue(event.getEntity().getWatchlist());
        }
    }

    @EventListener
    public void onAnimeStatusUpdated(AnimeWatchlistUpdatedEvent event) {

        if (event.getPrevious().shouldDisplayList()) {
            this.taskService.getFactory(WatchlistUpdateFactory.class).queue(event.getPrevious());
        }

        if (event.getCurrent().shouldDisplayList()) {
            this.taskService.getFactory(WatchlistUpdateFactory.class).queue(event.getCurrent());
        }

    }

    @EventListener({AnimeTotalUpdatedEvent.class, AnimeWatchedUpdatedEvent.class})
    public void onAnimeEpisodeValueUpdated(AnimeUpdatedEvent<Long> event) {

        Anime   anime            = event.getEntity();
        boolean hasBeenFinished  = anime.getWatched() == anime.getTotal();
        boolean isTaggedFinished = anime.getWatchlist() == AnimeStatus.WATCHED;

        if (hasBeenFinished && !isTaggedFinished) {
            this.animeService.mod(anime.getId(), entity -> entity.setWatchlist(AnimeStatus.WATCHED));
            return;
        }

        if (anime.getWatchlist().isShowProgress()) {
            this.taskService.getFactory(WatchlistUpdateFactory.class).queue(anime.getWatchlist());
        }
    }

    // </editor-fold>

    // <editor-fold desc="Broadcast">

    @EventListener
    public void onBroadcastCreated(BroadcastCreatedEvent event) {

        this.taskService.getFactory(BroadcastScheduleFactory.class).queue(event.getEntity());
    }

    @EventListener
    public void onBroadcastStatusUpdate(BroadcastStatusUpdatedEvent event) {

        Broadcast broadcast = event.getEntity();

        if (broadcast.shouldDoProgress()) {
            IAnime<?> anime = broadcast.getWatchTarget();

            switch (event.getCurrent()) {
                case ACTIVE -> this.animeService.mod(anime.getId(), this.animeService.defineWatching());
                case COMPLETED -> this.animeService.mod(
                        anime.getId(),
                        this.animeService.defineScheduleProgress(broadcast)
                );
            }
        }
    }

    // </editor-fold>

    // <editor-fold desc="User">

    @EventListener
    public void onUserEmoteUpdated(DiscordUserEmoteUpdatedEvent event) {

        // TODO: Optimisation possible, only select watchlist where the user has at least a single interest
        for (AnimeStatus status : AnimeStatus.getDisplayable()) {
            this.taskService.getFactory(WatchlistUpdateFactory.class).queue(status);
        }
    }

    // </editor-fold>

    // <editor-fold desc="Interest">

    @EventListener
    public void onInterestCreate(InterestCreatedEvent event) {

        this.taskService.getFactory(AnnouncementFactory.class).queue(event.getEntity().getAnime());
    }

    @EventListener
    public void onInterestUpdated(InterestLevelUpdatedEvent event) {

        this.taskService.getFactory(AnnouncementFactory.class).queue(event.getEntity().getAnime());
    }

    // </editor-fold>

    // <editor-fold desc="Selection">

    @EventListener
    public void onSelectionStateUpdated(SelectionStatusUpdatedEvent event) {

        List<Long> ids = switch (event.getCurrent()) {
            case OPEN -> Collections.emptyList();
            case CLOSED -> this.voterService
                    .getVoters(event.getEntity())
                    .stream()
                    .map(Voter::getVotes)
                    .flatMap(Set::stream)
                    .map(IAnime::getId)
                    .distinct()
                    .collect(Collectors.toList());
            case AUTO_CLOSED -> event.getEntity()
                                     .getAnimes()
                                     .stream()
                                     .map(IAnime::getId)
                                     .distinct()
                                     .collect(Collectors.toList());
        };

        this.animeService.move(ids, AnimeStatus.SIMULCAST);
    }

    // </editor-fold>

}
