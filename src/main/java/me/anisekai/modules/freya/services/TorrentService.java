package me.anisekai.modules.freya.services;

import fr.alexpado.lib.rest.exceptions.RestException;
import me.anisekai.modules.freya.configurations.FreyaConfiguration;
import me.anisekai.modules.freya.entities.detached.TranmissionTorrent;
import me.anisekai.modules.freya.interfaces.ITorrent;
import me.anisekai.modules.freya.lib.TransmissionDaemonClient;
import me.anisekai.modules.freya.services.data.TorrentDataService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class TorrentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TorrentService.class);

    private final TorrentDataService       service;
    private final TransmissionDaemonClient client;
    private final FreyaConfiguration       configuration;

    public TorrentService(TorrentDataService service, TransmissionDaemonClient client, FreyaConfiguration configuration) {

        this.service       = service;
        this.client        = client;
        this.configuration = configuration;
    }

    @Scheduled(cron = "* * * * * *")
    public void cron() {

        if (!this.configuration.isAutoDownloadEnabled()) {
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

            Map<Integer, TranmissionTorrent> torrentMap = new HashMap<>();
            for (int i = 0; i < torrents.length(); i++) {
                JSONObject         object  = torrents.getJSONObject(i);
                TranmissionTorrent torrent = new TranmissionTorrent(object);
                torrentMap.put(torrent.getId(), torrent);
            }

            this.service.getProxy().batch(torrentMap.keySet(), registeredTorrents -> {
                for (ITorrent registeredTorrent : registeredTorrents) {
                    TranmissionTorrent tranmissionTorrent = torrentMap.get(registeredTorrent.getId());
                    registeredTorrent.setStatus(tranmissionTorrent.getStatus());
                    registeredTorrent.setDownloadDir(tranmissionTorrent.getDownloadDir());
                    registeredTorrent.setPercentDone(tranmissionTorrent.getPercentDone());
                    registeredTorrent.setFile(tranmissionTorrent.getFile());
                }
            });
        } catch (Exception e) {
            if (e instanceof RestException restException) {
                LOGGER.warn(new String(restException.getBody(), StandardCharsets.UTF_8));
            }

            throw new RuntimeException(e);
        }
    }

}
