package me.anisekai.toshiko.services.misc;

import fr.alexpado.lib.rest.exceptions.RestException;
import me.anisekai.toshiko.configurations.AutoDownloadConfiguration;
import me.anisekai.toshiko.configurations.ToshikoFeatureConfiguration;
import me.anisekai.toshiko.data.RpcTorrent;
import me.anisekai.toshiko.entities.Torrent;
import me.anisekai.toshiko.events.misc.TorrentFinishedEvent;
import me.anisekai.toshiko.lib.TransmissionDaemonClient;
import me.anisekai.toshiko.repositories.TorrentRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class TorrentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TorrentService.class);

    private final ApplicationEventPublisher   publisher;
    private final TransmissionDaemonClient    client;
    private final ToshikoFeatureConfiguration featureConfiguration;
    private final TorrentRepository           repository;

    public TorrentService(ApplicationEventPublisher publisher, TransmissionDaemonClient client, ToshikoFeatureConfiguration featureConfiguration, AutoDownloadConfiguration configuration, TorrentRepository repository) {

        this.publisher = publisher;

        this.client = client;

        this.featureConfiguration = featureConfiguration;
        this.repository           = repository;
    }

    @Scheduled(cron = "0/1 * * * * *")
    public void cron() {

        if (!this.featureConfiguration.isAutoDownloadEnabled()) {
            return;
        }

        try {
            JSONObject queryResult = this.client.getTorrents();
            String     status      = queryResult.getString("result");

            if (!status.equals("success")) {
                LOGGER.warn("Failed to retrieve torrent list: RPC Response was '{}'.", status);
                return;
            }

            JSONObject arguments = queryResult.getJSONObject("arguments");
            JSONArray  torrents  = arguments.getJSONArray("torrents");

            Map<Integer, RpcTorrent> torrentMap = new HashMap<>();

            for (Object torrent : torrents) {
                RpcTorrent rpcTorrent = new RpcTorrent((JSONObject) torrent);
                torrentMap.put(rpcTorrent.getId(), rpcTorrent);
            }

            Iterable<Integer> allIds = torrentMap.keySet();

            List<Torrent> finished   = new ArrayList<>();
            Collection<Torrent> unfinished = new ArrayList<>();

            for (Torrent torrent : this.repository.findAllById(allIds)) {

                boolean wasFinished = torrent.getStatus().isFinished();
                torrent.update(torrentMap.get(torrent.getId()));

                this.repository.save(torrent);

                if (torrent.getStatus().isFinished() && !wasFinished) {
                    LOGGER.info("Torrent {} is now finished.", torrent.getId());
                    finished.add(torrent);
                } else if (!torrent.getStatus().isFinished()){
                    unfinished.add(torrent);
                }
            }

            for (int i = 0; i < finished.size(); i++) {
                Torrent torrent = finished.get(i);
                this.publisher.publishEvent(new TorrentFinishedEvent(this, torrent, !unfinished.isEmpty() && i+1 < finished.size()));
            }
        } catch (Exception e) {
            if (e instanceof RestException restException) {
                LOGGER.warn(new String(restException.getBody(), StandardCharsets.UTF_8));
            }

            throw new RuntimeException(e);
        }
    }
}
