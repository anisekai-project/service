package me.anisekai.modules.linn.services.data;

import me.anisekai.api.persistence.UpsertResult;
import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.linn.exceptions.anime.InvalidAnimeProgressException;
import me.anisekai.modules.linn.interfaces.IAnime;
import me.anisekai.modules.linn.repositories.AnimeRepository;
import me.anisekai.modules.shizue.interfaces.AnimeNightMeta;
import me.anisekai.modules.shizue.services.RateLimitedTaskService;
import me.anisekai.modules.linn.services.proxy.AnimeProxyService;
import me.anisekai.modules.toshiko.JdaStore;
import me.anisekai.modules.toshiko.tasks.SendAnnouncementTask;
import me.anisekai.modules.toshiko.tasks.UpdateAnnouncementTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class AnimeDataService extends DataService<Anime, Long, IAnime, AnimeRepository, AnimeProxyService> {

    private final RateLimitedTaskService rateLimitedTaskService;
    private final JdaStore               jdaStore;

    public AnimeDataService(AnimeProxyService proxy, RateLimitedTaskService rateLimitedTaskService, JdaStore jdaStore) {

        super(proxy);
        this.rateLimitedTaskService = rateLimitedTaskService;
        this.jdaStore               = jdaStore;
    }

    public List<Anime> getSimulcasts() {

        return this.getProxy().fetchEntities(repository -> repository.findAllByStatus(AnimeStatus.SIMULCAST_AVAILABLE));
    }

    public Consumer<IAnime> tagWatching() {

        return anime -> anime.setStatus(switch (anime.getStatus()) {
            case CANCELLED, WATCHED, WATCHING, DOWNLOADED, DOWNLOADING, NOT_DOWNLOADED, NO_SOURCE, UNAVAILABLE -> AnimeStatus.WATCHING;
            case SIMULCAST, SIMULCAST_AVAILABLE -> AnimeStatus.SIMULCAST;
            case STORAGE_ONLY -> AnimeStatus.STORAGE_ONLY;
        });
    }

    public Consumer<IAnime> progression(AnimeNightMeta broadcast) {

        return this.progression(broadcast.getLastEpisode(), null);
    }

    public Consumer<IAnime> progression(long watched, Long total) {

        return (anime) -> {
            if ((total != null && watched > total) || (anime.getTotal() > 0 && anime.getTotal() < watched)) {
                throw new InvalidAnimeProgressException();
            }

            anime.setWatched(watched);
            Optional.ofNullable(total).ifPresent(anime::setTotal);

            if (anime.getWatched() == anime.getTotal() && anime.getTotal() > 0) {
                anime.setStatus(AnimeStatus.WATCHED);
            }
        };
    }

    public Consumer<IAnime> announce() {

        return (anime) -> {
            // Will only work if announce message was previously set.
            anime.setAnnounceMessage(null);
        };
    }

    public void announce(Anime anime) {

        if (anime.getAnnounceMessage() == null) {
            this.rateLimitedTaskService.queue(new SendAnnouncementTask(this, this.jdaStore, anime));
        } else {
            this.rateLimitedTaskService.queue(new UpdateAnnouncementTask(this.jdaStore, anime));
        }
    }

    public UpsertResult<Anime> runImport(DiscordUser sender, JSONObject source) {

        JSONArray genreArray = source.getJSONArray("genres");
        JSONArray themeArray = source.getJSONArray("themes");
        String    rawStatus  = source.getString("status");

        List<String> genreList = new ArrayList<>();
        List<String> themeList = new ArrayList<>();
        genreArray.forEach(obj -> genreList.add(obj.toString()));
        themeArray.forEach(obj -> themeList.add(obj.toString()));

        String      name            = source.getString("title");
        String      synopsis        = source.getString("synopsis");
        String      genres          = String.join(", ", genreList);
        String      themes          = String.join(", ", themeList);
        AnimeStatus status          = AnimeStatus.from(rawStatus);
        String      link            = source.getString("link");
        String      image           = source.getString("image");
        long        total           = Long.parseLong(source.getString("episode"));
        long        episodeDuration = Long.parseLong(source.getString("time"));

        return this.getProxy().upsert(name, anime -> {
            anime.setSynopsis(synopsis);
            anime.setGenres(genres);
            anime.setThemes(themes);
            anime.setStatus(status);
            anime.setLink(link);
            anime.setImage(image);
            anime.setTotal(total);
            anime.setEpisodeDuration(episodeDuration);

            if (anime.getAddedBy() == null) anime.setAddedBy(sender);
        });
    }

}
