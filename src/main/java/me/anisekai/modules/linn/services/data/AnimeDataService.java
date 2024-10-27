package me.anisekai.modules.linn.services.data;

import me.anisekai.api.persistence.UpsertResult;
import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.globals.tasking.TaskingService;
import me.anisekai.globals.tasking.factories.AnnouncementCreateTaskFactory;
import me.anisekai.globals.tasking.factories.AnnouncementUpdateTaskFactory;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.linn.exceptions.anime.InvalidAnimeProgressException;
import me.anisekai.modules.linn.interfaces.IAnime;
import me.anisekai.modules.linn.repositories.AnimeRepository;
import me.anisekai.modules.linn.services.proxy.AnimeProxyService;
import me.anisekai.modules.shizue.interfaces.AnimeNightMeta;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class AnimeDataService extends DataService<Anime, Long, IAnime, AnimeRepository, AnimeProxyService> {

    private final TaskingService taskingService;

    public AnimeDataService(AnimeProxyService proxy, TaskingService taskingService) {

        super(proxy);
        this.taskingService = taskingService;
    }

    public List<Anime> getSimulcasts() {

        return this.getProxy().fetchEntities(repository -> repository.findAllByStatus(AnimeStatus.SIMULCAST_AVAILABLE));
    }

    public Consumer<IAnime> tagWatching() {

        return anime -> anime.setStatus(switch (anime.getStatus()) {
            case CANCELLED, WATCHED, WATCHING, DOWNLOADED, DOWNLOADING, NOT_DOWNLOADED, NO_SOURCE,
                 UNAVAILABLE -> AnimeStatus.WATCHING;
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

    public void announce(IAnime anime) {

        if (anime.getAnnounceMessage() == null) {
            AnnouncementCreateTaskFactory.queue(this.taskingService, anime);
        } else {
            AnnouncementUpdateTaskFactory.queue(this.taskingService, anime);
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
