package me.anisekai.server.services;

import fr.anisekai.wireless.api.plannifier.interfaces.ScheduleSpotData;
import fr.anisekai.wireless.remote.enums.AnimeList;
import me.anisekai.server.entities.adapters.AnimeEventAdapter;
import me.anisekai.server.persistence.DataService;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.AnimeCreatedEvent;
import me.anisekai.server.persistence.UpsertResult;
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
public class AnimeService extends DataService<Anime, Long, AnimeEventAdapter, AnimeRepository, AnimeProxy> {

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

        String    name            = source.getString("title");
        String    synopsis        = source.getString("synopsis");
        AnimeList status          = AnimeList.from(rawStatus);
        String    link            = source.getString("link");
        String    image           = source.getString("image");
        long      total           = Long.parseLong(source.getString("episode"));
        long      episodeDuration = Long.parseLong(source.getString("time"));

        return this.getProxy().upsert(
                repo -> repo.findByTitle(name)
                , anime -> {
                    anime.setTitle(name);
                    anime.setList(status);
                    anime.setSynopsis(synopsis);
                    anime.setTags(tagList);
                    anime.setThumbnailUrl(image);
                    anime.setUrl(link);
                    anime.setTotal(total);
                    anime.setEpisodeDuration(episodeDuration);
                    //noinspection ConstantValue — This is an UPSERT context, this can *really* be null.
                    if (anime.getAddedBy() == null) anime.setAddedBy(sender);
                },
                AnimeCreatedEvent::new
        );
    }

    public List<Anime> getAnimesAddedByUser(DiscordUser user) {

        return this.fetchAll(repo -> repo.findByAddedBy(user));
    }

    public List<Anime> getOfStatus(AnimeList status) {

        return this.fetchAll(repo -> repo.findAllByList(status));
    }

    public List<Anime> getSimulcastsAvailable() {

        return this.getOfStatus(AnimeList.SIMULCAST_AVAILABLE);
    }

    public List<Anime> getAllDownloadable() {

        return this.fetchAll(AnimeRepository::findAllByTitleRegexIsNotNull);
    }

    public List<Anime> move(Collection<Long> ids, AnimeList to) {

        if (ids.isEmpty()) return Collections.emptyList();

        return this.batch(
                repository -> repository.findAllById(ids),
                entity -> entity.setList(to)
        );
    }

    public List<Anime> move(AnimeList from, AnimeList to) {

        return this.batch(
                repository -> repository.findAllByList(from),
                entity -> entity.setList(to)
        );
    }

    public Consumer<AnimeEventAdapter> defineProgression(long progression) {

        return entity -> {
            entity.setWatched(progression);
            if (entity.getTotal() == progression) {
                entity.setList(AnimeList.WATCHED);
            }
        };
    }

    public Consumer<AnimeEventAdapter> defineProgression(long progression, long total) {

        return entity -> {
            entity.setWatched(progression);
            entity.setTotal(total);
            if (entity.getTotal() == progression) {
                entity.setList(AnimeList.WATCHED);
            }
        };
    }

    public Consumer<AnimeEventAdapter> defineWatching() {

        return anime -> {
            switch (anime.getList()) {
                case WATCHED,
                     DOWNLOADED,
                     DOWNLOADING,
                     NOT_DOWNLOADED,
                     NO_SOURCE,
                     UNAVAILABLE,
                     CANCELLED -> anime.setList(AnimeList.WATCHING);
                case SIMULCAST_AVAILABLE -> anime.setList(AnimeList.SIMULCAST);
            }
        };
    }

    public Consumer<AnimeEventAdapter> defineScheduleProgress(ScheduleSpotData<?> broadcast) {

        return entity -> this.defineProgression(entity.getWatched() + broadcast.getEpisodeCount()).accept(entity);
    }

}
