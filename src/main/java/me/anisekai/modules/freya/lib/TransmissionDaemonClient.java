package me.anisekai.modules.freya.lib;

import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.transmission.TransmissionClient;
import me.anisekai.modules.freya.configurations.FreyaConfiguration;
import me.anisekai.modules.freya.entities.detached.NyaaRssEntry;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TransmissionDaemonClient {

    private final TransmissionClient client;

    public TransmissionDaemonClient(FreyaConfiguration configuration) {

        this.client = new TransmissionClient(configuration.getRpc());
    }

    public BookshelfJson getTorrents() throws Exception {

        // TODO: Manage the result and "cast" it to Nyaa item.
        return this.client.getTorrents();
    }

    public JSONObject startTorrent(NyaaRssEntry entry) throws Exception {

        // TODO: Check the JSON return of this, we might be able to create the nyaa item right now
        return this.client.startTorrent(entry.getTorrent());
    }

}
