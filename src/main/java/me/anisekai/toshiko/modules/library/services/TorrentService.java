package me.anisekai.toshiko.modules.library.services;

import fr.alexpado.lib.rest.exceptions.RestException;
import me.anisekai.toshiko.configurations.ToshikoFeatureConfiguration;
import me.anisekai.toshiko.data.RpcTorrent;
import me.anisekai.toshiko.interfaces.entities.ITorrent;
import me.anisekai.toshiko.modules.library.lib.TransmissionDaemonClient;
import me.anisekai.toshiko.services.data.TorrentDataService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TorrentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TorrentService.class);

    private final TorrentDataService          service;
    private final TransmissionDaemonClient    client;
    private final ToshikoFeatureConfiguration featureConfiguration;

    public TorrentService(TorrentDataService service, TransmissionDaemonClient client, ToshikoFeatureConfiguration featureConfiguration) {

        this.service              = service;
        this.client               = client;
        this.featureConfiguration = featureConfiguration;
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

            Map<Integer, RpcTorrent> torrentMap = torrents.toList().stream()
                                                          .filter(JSONObject.class::isInstance)
                                                          .map(JSONObject.class::cast)
                                                          .map(RpcTorrent::new)
                                                          .collect(Collectors.toMap(RpcTorrent::getId, rpc -> rpc));

            this.service.getProxy().batch(torrentMap.keySet(), registeredTorrents -> {
                for (ITorrent registeredTorrent : registeredTorrents) {
                    RpcTorrent rpcTorrent = torrentMap.get(registeredTorrent.getId());
                    registeredTorrent.setStatus(rpcTorrent.getStatus());
                    registeredTorrent.setDownloadDir(rpcTorrent.getDownloadDir());
                    registeredTorrent.setPercentDone(rpcTorrent.getPercentDone());
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
