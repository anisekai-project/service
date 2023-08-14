package me.anisekai.toshiko.services.data;

import me.anisekai.toshiko.data.RpcTorrent;
import me.anisekai.toshiko.entities.Torrent;
import me.anisekai.toshiko.interfaces.entities.ITorrent;
import me.anisekai.toshiko.repositories.TorrentRepository;
import me.anisekai.toshiko.services.AbstractDataService;
import me.anisekai.toshiko.services.proxy.TorrentProxyService;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class TorrentDataService extends AbstractDataService<Torrent, Integer, ITorrent, TorrentRepository, TorrentProxyService> {


    public TorrentDataService(TorrentProxyService proxy) {

        super(proxy);
    }

    public Consumer<ITorrent> update(RpcTorrent rpcTorrent) {

        return torrent -> {
            torrent.setStatus(rpcTorrent.getStatus());
            torrent.setDownloadDir(rpcTorrent.getDownloadDir());
            torrent.setPercentDone(rpcTorrent.getPercentDone());
        };
    }

}
