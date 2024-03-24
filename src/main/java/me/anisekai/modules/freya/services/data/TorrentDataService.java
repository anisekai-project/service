package me.anisekai.modules.freya.services.data;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.modules.freya.entities.Torrent;
import me.anisekai.modules.freya.entities.detached.TranmissionTorrent;
import me.anisekai.modules.freya.interfaces.ITorrent;
import me.anisekai.modules.freya.repositories.TorrentRepository;
import me.anisekai.modules.freya.services.proxy.TorrentProxyService;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class TorrentDataService extends DataService<Torrent, Integer, ITorrent, TorrentRepository, TorrentProxyService> {


    public TorrentDataService(TorrentProxyService proxy) {

        super(proxy);
    }

    public Consumer<ITorrent> update(TranmissionTorrent tranmissionTorrent) {

        return torrent -> {
            torrent.setStatus(tranmissionTorrent.getStatus());
            torrent.setDownloadDir(tranmissionTorrent.getDownloadDir());
            torrent.setPercentDone(tranmissionTorrent.getPercentDone());
        };
    }

}
