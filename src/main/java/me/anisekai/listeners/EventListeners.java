package me.anisekai.listeners;

import fr.anisekai.wireless.remote.enums.AnimeList;
import fr.anisekai.wireless.remote.enums.BroadcastStatus;
import fr.anisekai.wireless.remote.interfaces.AnimeEntity;
import me.anisekai.discord.tasks.anime.announcement.AnnouncementFactory;
import me.anisekai.discord.tasks.broadcast.schedule.BroadcastScheduleFactory;
import me.anisekai.discord.tasks.watchlist.update.WatchlistUpdateFactory;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.Broadcast;
import me.anisekai.server.entities.Voter;
import me.anisekai.server.events.*;
import me.anisekai.server.events.anime.*;
import me.anisekai.server.events.broadcast.BroadcastEpisodeCountUpdatedEvent;
import me.anisekai.server.events.broadcast.BroadcastFirstEpisodeUpdatedEvent;
import me.anisekai.server.events.broadcast.BroadcastStartingAtUpdatedEvent;
import me.anisekai.server.events.broadcast.BroadcastStatusUpdatedEvent;
import me.anisekai.server.events.interest.InterestLevelUpdatedEvent;
import me.anisekai.server.events.selection.SelectionStatusUpdatedEvent;
import me.anisekai.server.events.user.UserEmoteUpdatedEvent;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.BroadcastService;
import me.anisekai.server.services.TaskService;
import me.anisekai.server.services.VoterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger LOGGER = LoggerFactory.getLogger(EventListeners.class);

    private final TaskService      taskService;
    private final AnimeService     animeService;
    private final VoterService     voterService;
    private final BroadcastService broadcastService;

    public EventListeners(TaskService taskService, AnimeService animeService, VoterService voterService, BroadcastService broadcastService) {

        this.taskService      = taskService;
        this.animeService     = animeService;
        this.voterService     = voterService;
        this.broadcastService = broadcastService;
    }

    // <editor-fold desc="Anime">

    @EventListener
    public void onAnimeCreated(AnimeCreatedEvent event) {

        this.taskService.getFactory(AnnouncementFactory.class).queue(event.getEntity());

        if (event.getEntity().getList().hasProperty(AnimeList.Property.SHOW)) {
            this.taskService.getFactory(WatchlistUpdateFactory.class).queue(event.getEntity().getList());
        }
    }

    @EventListener({
            AnimeUrlUpdatedEvent.class,
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
            AnimeUrlUpdatedEvent.class,
            AnimeTitleUpdatedEvent.class
    })
    public void onAnimeDataUpdated(AnimeUpdatedEvent<?> event) {

        if (event.getEntity().getList().hasProperty(AnimeList.Property.SHOW)) {
            this.taskService.getFactory(WatchlistUpdateFactory.class).queue(event.getEntity().getList());
        }
    }

    @EventListener
    public void onAnimeStatusUpdated(AnimeListUpdatedEvent event) {

        if (event.getOldValue().hasProperty(AnimeList.Property.SHOW)) {
            this.taskService.getFactory(WatchlistUpdateFactory.class).queue(event.getOldValue());
        }

        if (event.getNewValue().hasProperty(AnimeList.Property.SHOW)) {
            this.taskService.getFactory(WatchlistUpdateFactory.class).queue(event.getNewValue());
        }

    }

    @EventListener({AnimeTotalUpdatedEvent.class, AnimeWatchedUpdatedEvent.class})
    public void onAnimeEpisodeValueUpdated(AnimeUpdatedEvent<Long> event) {

        Anime   anime            = event.getEntity();
        boolean hasBeenFinished  = anime.getWatched() == anime.getTotal();
        boolean isTaggedFinished = anime.getList() == AnimeList.WATCHED;

        if (hasBeenFinished && !isTaggedFinished) {
            this.animeService.mod(anime.getId(), entity -> entity.setList(AnimeList.WATCHED));
            return;
        }

        if (anime.getList().hasProperty(AnimeList.Property.PROGRESS)) {
            this.taskService.getFactory(WatchlistUpdateFactory.class).queue(anime.getList());
        }

        if (event instanceof AnimeWatchedUpdatedEvent watchedUpdatedEvent) {
            if (watchedUpdatedEvent.getOldValue() == 0 && watchedUpdatedEvent.getNewValue() > 0) {
                this.animeService.mod(anime.getId(), this.animeService.defineWatching());
            }
        }
    }

    // </editor-fold>

    // <editor-fold desc="Broadcast">

    @EventListener
    public void onBroadcastCreated(BroadcastCreatedEvent event) {

        if (!this.broadcastService.hasPreviousScheduled(event.getEntity())) {
            this.taskService.getFactory(BroadcastScheduleFactory.class).queue(event.getEntity());
        } else {
            LOGGER.info("Broadcast {} set to be scheduled later.", event.getEntity().getId());
        }
    }

    @EventListener
    public void onBroadcastStatusUpdate(BroadcastStatusUpdatedEvent event) {

        Broadcast broadcast = event.getEntity();
        if (event.getNewValue() == BroadcastStatus.CANCELED) {
            return;
        }

        AnimeEntity<?> anime = broadcast.getWatchTarget();

        switch (event.getNewValue()) {
            case ACTIVE -> this.animeService.mod(anime.getId(), this.animeService.defineWatching());
            case COMPLETED -> this.animeService.mod(
                    anime.getId(),
                    this.animeService.defineScheduleProgress(broadcast)
            );
        }

        // Special treatment for unscheduled events that are waiting.
        // Keeping it separated from the logic above
        if (event.getNewValue() == BroadcastStatus.CANCELED || event.getNewValue() == BroadcastStatus.COMPLETED) {
            List<Broadcast> broadcasts = this.broadcastService
                    .getProxy()
                    .getRepository()
                    .findByWatchTargetAndStartingAtAfterOrderByStartingAtAsc(
                            broadcast.getWatchTarget(),
                            broadcast.getStartingAt()
                    );

            if (!broadcasts.isEmpty()) {
                Broadcast first = broadcasts.getFirst();
                this.taskService.getFactory(BroadcastScheduleFactory.class).queue(first);
            }
        }
    }

    @EventListener({
            BroadcastFirstEpisodeUpdatedEvent.class,
            BroadcastStartingAtUpdatedEvent.class,
            BroadcastEpisodeCountUpdatedEvent.class,
    })
    public void onBroadcastStateUpdated(BroadcastUpdatedEvent<?> event) {

        if (event.getEntity().getStatus() == BroadcastStatus.SCHEDULED) {
            this.taskService.getFactory(BroadcastScheduleFactory.class).queue(event.getEntity());
        }
    }

    // </editor-fold>

    // <editor-fold desc="User">

    @EventListener
    public void onUserEmoteUpdated(UserEmoteUpdatedEvent event) {

        // TODO: Optimisation possible, only select watchlist where the user has at least a single interest
        for (AnimeList status : AnimeList.collect(AnimeList.Property.SHOW)) {
            this.taskService.getFactory(WatchlistUpdateFactory.class).queue(status);
        }
    }

    // </editor-fold>

    // <editor-fold desc="Interest">

    @EventListener
    public void onInterestCreate(InterestCreatedEvent event) {

        this.taskService.getFactory(AnnouncementFactory.class).queue(event.getEntity().getAnime());
        this.taskService.getFactory(WatchlistUpdateFactory.class).queue(event.getEntity().getAnime().getList());
    }

    @EventListener
    public void onInterestUpdated(InterestLevelUpdatedEvent event) {

        this.taskService.getFactory(AnnouncementFactory.class).queue(event.getEntity().getAnime());
        this.taskService.getFactory(WatchlistUpdateFactory.class).queue(event.getEntity().getAnime().getList());
    }

    // </editor-fold>

    // <editor-fold desc="Selection">

    @EventListener
    public void onSelectionStateUpdated(SelectionStatusUpdatedEvent event) {

        List<Long> ids = switch (event.getNewValue()) {
            case OPEN -> Collections.emptyList();
            case CLOSED -> this.voterService
                    .getVoters(event.getEntity())
                    .stream()
                    .map(Voter::getVotes)
                    .flatMap(Set::stream)
                    .map(AnimeEntity::getId)
                    .distinct()
                    .collect(Collectors.toList());
            case AUTO_CLOSED -> event.getEntity()
                                     .getAnimes()
                                     .stream()
                                     .map(AnimeEntity::getId)
                                     .distinct()
                                     .collect(Collectors.toList());
        };

        this.animeService.move(ids, AnimeList.SIMULCAST);
    }

    // </editor-fold>

}
