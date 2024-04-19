package me.anisekai.modules.freya.services;

import me.anisekai.modules.freya.configurations.FreyaConfiguration;
import me.anisekai.modules.freya.entities.Torrent;
import me.anisekai.modules.freya.entities.detached.NyaaRssEntry;
import me.anisekai.modules.freya.enums.TorrentStatus;
import me.anisekai.modules.freya.events.torrent.TorrentCreatedEvent;
import me.anisekai.modules.freya.lib.RSSAnalyzer;
import me.anisekai.modules.freya.lib.TransmissionDaemonClient;
import me.anisekai.modules.freya.services.data.TorrentDataService;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.repositories.AnimeRepository;
import me.anisekai.modules.shizue.helpers.TaskHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RSSService extends TaskHandler<List<NyaaRssEntry>> {

    private final static Logger LOGGER = LoggerFactory.getLogger(RSSService.class);

    private final FreyaConfiguration       configuration;
    private final AnimeRepository          animeRepository;
    private final TransmissionDaemonClient client;
    private final TorrentDataService       service;

    public RSSService(FreyaConfiguration configuration, AnimeRepository animeRepository, TransmissionDaemonClient client, TorrentDataService service) {

        // TODO: Move the cron expression in settings
        super("RSS", "0 0/10 * * * *");
        this.configuration   = configuration;
        this.animeRepository = animeRepository;
        this.client          = client;
        this.service         = service;
    }

    @Scheduled(cron = "* * * * * *")
    public void doTick() {

        this.tick();
    }

    @Override
    public List<NyaaRssEntry> execute() {

        if (!this.configuration.isAutoDownloadEnabled()) {
            LOGGER.debug("Auto-download disabled due to configuration policy.");
            return Collections.emptyList();
        }

        List<Anime> downloadableAnime = this.animeRepository.findAllAutoDownloadReady();

        if (downloadableAnime.isEmpty()) {
            LOGGER.info("No anime to download auto-magically.");
            return Collections.emptyList();
        }

        try {

            LOGGER.info("Reading RSS feed...");
            URI                       uri      = new URI(this.configuration.getRss());
            RSSAnalyzer<NyaaRssEntry> rss      = new RSSAnalyzer<>(uri, NyaaRssEntry::new);
            List<NyaaRssEntry>        rssItems = rss.analyze();


            LOGGER.info("Filtering results...");
            List<String> hashes = this.service.fetchAll().stream().map(Torrent::getInfoHash).toList();
            List<String> titles = downloadableAnime.stream().map(Anime::getRssMatch).toList();

            rssItems.removeIf(entry -> hashes.contains(entry.getInfoHash()));
            rssItems.removeIf(entry -> titles.stream().noneMatch(entry.getTitle()::contains));
            rssItems.removeIf(entry -> !entry.getTitle().contains("1080p") || !entry.getTitle()
                                                                                    .contains("VOSTFR") || entry.getTitle()
                                                                                                                .contains(
                                                                                                                        "(CUSTOM)"));

            LOGGER.info("There is {} downloadable content.", rssItems.size());


            LOGGER.info("Analyzing entries...");
            for (NyaaRssEntry rssItem : rssItems) {
                LOGGER.debug("Analyzing {}...", rssItem.getTitle());

                // Do we have a match ?
                Optional<Anime> optionalAnime = downloadableAnime
                        .stream()
                        .filter(anime -> rssItem.getTitle().contains(anime.getRssMatch()))
                        .findAny();

                if (optionalAnime.isEmpty()) {
                    continue; // No matching anime.
                }

                LOGGER.info("Starting download for {}", rssItem.getLink());
                JSONObject response    = this.client.startTorrent(rssItem);
                String     queryResult = response.getString("result");

                if (!queryResult.equals("success")) {
                    LOGGER.warn("Failed to start torrent (code {})", queryResult);
                    continue;
                }

                JSONObject arguments = response.getJSONObject("arguments");
                JSONObject torrentData;
                if (arguments.has("torrent-duplicate")) {
                    LOGGER.info("{} was already downloaded", rssItem.getLink());
                    torrentData = arguments.getJSONObject("torrent-duplicate");
                } else if (arguments.has("torrent-added")) {
                    LOGGER.info("Started downloading {}", rssItem.getLink());
                    torrentData = arguments.getJSONObject("torrent-added");
                } else {
                    continue;
                }

                this.service.getProxy().create(torrent -> {
                    torrent.setId(torrentData.getInt("id"));
                    torrent.setAnime(optionalAnime.get());
                    torrent.setLink(rssItem.getLink());
                    torrent.setName(rssItem.getTitle());
                    torrent.setStatus(TorrentStatus.VERIFY_QUEUED);
                    torrent.setPercentDone(0);
                    torrent.setInfoHash(rssItem.getInfoHash());
                }, TorrentCreatedEvent::new);
            }

            LOGGER.info("Task finished.");
            return rssItems;
        } catch (Exception e) {
            LOGGER.error("Failure", e);
        }
        return Collections.emptyList();
    }

}
