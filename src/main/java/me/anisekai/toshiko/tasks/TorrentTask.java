package me.anisekai.toshiko.tasks;

import me.anisekai.toshiko.entities.Torrent;
import me.anisekai.toshiko.helpers.RPC;
import me.anisekai.toshiko.repositories.TorrentRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TorrentTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(TorrentTask.class);

    private final RPC               client;
    private final TorrentRepository repository;

    public TorrentTask(RPC client, TorrentRepository repository) {

        this.client     = client;
        this.repository = repository;
    }

    //@Scheduled(fixedDelay = 500)
    @Transactional
    public void cacheData() {

        try {
            JSONObject results  = this.client.getTorrents();
            JSONArray  torrents = results.getJSONObject("arguments").getJSONArray("torrents");

            for (int i = 0 ; i < torrents.length() ; i++) {
                JSONObject torrentSource = torrents.getJSONObject(i);
                Torrent    torrent       = new Torrent(torrentSource);
                this.repository.save(torrent);
            }
        } catch (Exception e) {
            LOGGER.error("Oops", e);
        }
    }

}
