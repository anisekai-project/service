package me.anisekai.library.services;

import me.anisekai.api.transmission.NyaaRssEntry;
import me.anisekai.api.transmission.TransmissionClient;
import me.anisekai.api.transmission.TransmissionTorrent;
import me.anisekai.server.services.SettingService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class SpringTransmissionClient {

    private final SettingService     settingService;
    private       TransmissionClient client;

    public SpringTransmissionClient(SettingService settingService) {

        this.settingService = settingService;
    }

    /**
     * This method allow the current instance to self-check the state of the underlying transmission client.
     */
    public void check() {

        Optional<String> optionalEndpoint = this.settingService.getDownloadServer();

        if (optionalEndpoint.isEmpty()) {
            this.client = null;
            return;
        }

        String server = optionalEndpoint.get();

        if (this.client != null && this.client.getEndpoint().equals(server)) {
            return;
        }

        this.client = new TransmissionClient(server);
    }

    public boolean isAvailable() {

        return this.client != null;
    }

    public Set<TransmissionTorrent> getTorrents() throws Exception {

        return this.client.queryTorrents();
    }

    public TransmissionTorrent offerTorrent(NyaaRssEntry entry) throws Exception {

        return this.client.offerTorrent(entry);
    }

}
