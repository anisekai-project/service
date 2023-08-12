package me.anisekai.toshiko.modules.library.services;

import me.anisekai.toshiko.configurations.AutoDownloadConfiguration;
import me.anisekai.toshiko.configurations.ToshikoFeatureConfiguration;
import me.anisekai.toshiko.data.NyaaRssItem;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.Torrent;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.TorrentStatus;
import me.anisekai.toshiko.events.torrent.TorrentCreatedEvent;
import me.anisekai.toshiko.modules.library.lib.RSSAnalyzer;
import me.anisekai.toshiko.modules.library.lib.TransmissionDaemonClient;
import me.anisekai.toshiko.repositories.AnimeRepository;
import me.anisekai.toshiko.services.data.TorrentDataService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RSSService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RSSService.class);

    private final ToshikoFeatureConfiguration featureConfiguration;
    private final AutoDownloadConfiguration   configuration;
    private final AnimeRepository             animeRepository;
    private final TransmissionDaemonClient    client;
    private final TorrentDataService          service;

    public RSSService(ToshikoFeatureConfiguration featureConfiguration, AutoDownloadConfiguration configuration, AnimeRepository animeRepository, TransmissionDaemonClient client, TorrentDataService service) {

        this.featureConfiguration = featureConfiguration;
        this.configuration        = configuration;
        this.animeRepository      = animeRepository;
        this.client               = client;
        this.service              = service;
    }

    // Every 2m
    @Scheduled(cron = "0 0/10 * * * *")
    public void cron() {

        if (!this.featureConfiguration.isAutoDownloadEnabled()) {
            LOGGER.debug("Auto-download disabled due to configuration policy.");
            return;
        }

        List<Anime> downloadableAnime = this.animeRepository.findAllAutoDownloadReady(AnimeStatus.SIMULCAST);

        if (downloadableAnime.isEmpty()) {
            LOGGER.info("No anime to download auto-magically.");
            return;
        }

        try {

            LOGGER.info("Reading RSS feed...");
            URI                      uri      = new URI(this.configuration.getRss());
            RSSAnalyzer<NyaaRssItem> rss      = new RSSAnalyzer<>(uri, NyaaRssItem::new);
            List<NyaaRssItem>        rssItems = rss.analyze();


            LOGGER.info("Filtering results...");
            List<String> hashes = this.service.fetchAll().stream().map(Torrent::getInfoHash).toList();
            List<String> titles = downloadableAnime.stream().map(Anime::getRssMatch).toList();

            rssItems.removeIf(entry -> hashes.contains(entry.getInfoHash()));
            rssItems.removeIf(entry -> titles.stream().noneMatch(entry.getTitle()::contains));
            rssItems.removeIf(entry -> !entry.getTitle().contains("1080p") || !entry.getTitle()
                                                                                    .contains("VOSTFR") || entry.getTitle()
                                                                                                                .contains(
                                                                                                                        "(CUSTOM)"));

            LOGGER.info("Analyzing entries...");
            List<Torrent> startedTorrents = new ArrayList<>();
            for (NyaaRssItem rssItem : rssItems) {

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
        } catch (Exception e) {
            LOGGER.error("Failure", e);
        }
    }

}
