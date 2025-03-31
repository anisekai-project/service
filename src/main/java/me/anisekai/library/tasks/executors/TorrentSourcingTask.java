package me.anisekai.library.tasks.executors;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.nyaa.NyaaFeed;
import me.anisekai.api.transmission.NyaaRssEntry;
import me.anisekai.library.services.SpringTransmissionClient;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.Episode;
import me.anisekai.server.entities.Torrent;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.EpisodeService;
import me.anisekai.server.services.TorrentService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.utils.MapUtils;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TorrentSourcingTask implements TaskExecutor {

    public static final String OPTION_SOURCE = "source";

    private final AnimeService   animeService;
    private final EpisodeService episodeService;
    private final TorrentService torrentService;

    public TorrentSourcingTask(AnimeService animeService, EpisodeService episodeService, TorrentService torrentService) {

        this.animeService   = animeService;
        this.episodeService = episodeService;
        this.torrentService = torrentService;
    }

    /**
     * Check if the executor can find the required content in the provide {@link BookshelfJson} for its execution.
     *
     * @param params
     *         A {@link BookshelfJson}
     *
     * @return True if the json contains all settings, false otherwise.
     */
    @Override
    public boolean validateParams(BookshelfJson params) {

        return params.has(OPTION_PRIORITY) && params.has(OPTION_SOURCE);
    }

    /**
     * Run this task.
     *
     * @param timer
     *         The timer to use to mesure performance of the task.
     * @param params
     *         The parameters of this task.
     *
     * @throws Exception
     *         Thew if something happens.
     */
    @Override
    public void execute(ITimedAction timer, BookshelfJson params) throws Exception {

        long   priority = params.getLong(OPTION_PRIORITY);
        String source   = params.getString(OPTION_SOURCE);

        timer.action("client-check", "Check the transmission client");
        SpringTransmissionClient client = this.torrentService.getClient();
        client.check();
        if (!client.isAvailable()) throw new IllegalArgumentException("Transmission client is not available");
        timer.endAction();

        timer.action("load-entities", "Load entities from database");
        List<Anime> animes = this.animeService.getAllDownloadable();
        Map<Long, Anime> animeMap = animes
                .stream()
                .collect(MapUtils.map(Anime::getId, anime -> anime));

        Map<Long, Pattern> regexMap = animes
                .stream()
                .filter(anime -> anime.getTitleRegex() != null)
                .collect(MapUtils.map(Anime::getId, anime -> Pattern.compile(anime.getTitleRegex())));
        timer.endAction();

        timer.action("rss-load", "Reading RSS content");
        URI                uri     = URI.create(source);
        List<NyaaRssEntry> entries = NyaaFeed.analyze(uri, NyaaRssEntry::new);
        timer.endAction();

        timer.action("rss-handle", "Handling RSS content");
        for (NyaaRssEntry entry : entries) {
            timer.action("find-rss-match", entry.getLink());

            for (Map.Entry<Long, Pattern> mapEntry : regexMap.entrySet()) {
                Matcher matcher = mapEntry.getValue().matcher(entry.getTitle());
                if (matcher.matches()) {
                    Anime anime  = animeMap.get(mapEntry.getKey());
                    int   number = Integer.parseInt(matcher.group("episode"));

                    Optional<Episode> optionalEpisode = this.episodeService.getEpisode(anime, number);

                    if (optionalEpisode.isPresent() && this.torrentService.getTorrent(optionalEpisode.get())
                                                                          .isPresent()) {
                        // Download already up and running — or probably already done, in fact.
                        continue;
                    }

                    timer.action("submit-torrent", "Submitting the torrent");
                    Episode episode = optionalEpisode.orElseGet(() -> this.episodeService.create(anime, number));
                    Torrent torrent = this.torrentService.download(entry, episode, priority);
                    timer.endAction();
                    break;
                }
            }
            timer.endAction();
        }
        timer.endAction();
    }

}
