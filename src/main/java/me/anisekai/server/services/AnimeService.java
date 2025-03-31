package me.anisekai.server.services;

import me.anisekai.api.persistence.UpsertResult;
import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.api.plannifier.interfaces.ScheduleSpotData;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.enums.AnimeStatus;
import me.anisekai.server.events.AnimeCreatedEvent;
import me.anisekai.server.interfaces.IAnime;
import me.anisekai.server.proxy.AnimeProxy;
import me.anisekai.server.repositories.AnimeRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Service
public class AnimeService extends DataService<Anime, Long, IAnime<DiscordUser>, AnimeRepository, AnimeProxy> {

    public AnimeService(AnimeProxy proxy) {

        super(proxy);
    }

    public UpsertResult<Anime> importAnime(DiscordUser sender, JSONObject source) {

        JSONArray genreArray = source.getJSONArray("genres");
        JSONArray themeArray = source.getJSONArray("themes");
        String    rawStatus  = source.getString("status");

        List<String> tagList = new ArrayList<>();
        genreArray.forEach(obj -> tagList.add(obj.toString()));
        themeArray.forEach(obj -> tagList.add(obj.toString()));

        String      name            = source.getString("title");
        String      synopsis        = source.getString("synopsis");
        String      tags            = String.join(", ", tagList);
        AnimeStatus status          = AnimeStatus.from(rawStatus);
        String      link            = source.getString("link");
        String      image           = source.getString("image");
        long        total           = Long.parseLong(source.getString("episode"));
        long        episodeDuration = Long.parseLong(source.getString("time"));

        return this.getProxy().upsert(
                repo -> repo.findByTitle(name)
                , anime -> {
                    anime.setTitle(name);
                    anime.setWatchlist(status);
                    anime.setSynopsis(synopsis);
                    anime.setTags(tags);
                    anime.setThumbnail(image);
                    anime.setNautiljonUrl(link);
                    if (anime.getAddedBy() == null) anime.setAddedBy(sender);
                },
                AnimeCreatedEvent::new
        );
    }

    public List<Anime> getAnimesAddedByUser(DiscordUser user) {

        return this.fetchAll(repo -> repo.findByAddedBy(user));
    }

    public List<Anime> getOfStatus(AnimeStatus status) {

        return this.fetchAll(repo -> repo.findAllByStatus(status));
    }

    public List<Anime> getSimulcastsAvailable() {

        return this.getOfStatus(AnimeStatus.SIMULCAST_AVAILABLE);
    }

    public List<Anime> getAllDownloadable() {

        return this.fetchAll(AnimeRepository::findAllByTitleRegexIsNotNull);
    }

    public List<Anime> move(Collection<Long> ids, AnimeStatus to) {

        if (ids.isEmpty()) return Collections.emptyList();

        return this.batch(
                repository -> repository.findAllById(ids),
                entity -> entity.setWatchlist(to)
        );
    }

    public List<Anime> move(AnimeStatus from, AnimeStatus to) {

        return this.batch(
                repository -> repository.findAllByStatus(from),
                entity -> entity.setWatchlist(to)
        );
    }

    public Consumer<IAnime<DiscordUser>> defineProgression(long progression) {

        return entity -> {
            entity.setWatched(progression);
            if (entity.getTotal() == progression) {
                entity.setWatchlist(AnimeStatus.WATCHED);
            }
        };
    }

    public Consumer<IAnime<DiscordUser>> defineProgression(long progression, long total) {

        return entity -> {
            entity.setWatched(progression);
            entity.setTotal(total);
            if (entity.getTotal() == progression) {
                entity.setWatchlist(AnimeStatus.WATCHED);
            }
        };
    }

    public Consumer<IAnime<DiscordUser>> defineWatching() {

        return anime -> {
            switch (anime.getWatchlist()) {
                case WATCHED,
                     DOWNLOADED,
                     DOWNLOADING,
                     NOT_DOWNLOADED,
                     NO_SOURCE,
                     UNAVAILABLE,
                     CANCELLED -> anime.setWatchlist(AnimeStatus.WATCHING);
                case SIMULCAST_AVAILABLE -> anime.setWatchlist(AnimeStatus.SIMULCAST);
            }
        };
    }

    public Consumer<IAnime<DiscordUser>> defineScheduleProgress(ScheduleSpotData<?> broadcast) {

        return entity -> this.defineProgression(entity.getWatched() + broadcast.getEpisodeCount()).accept(entity);
    }

}
