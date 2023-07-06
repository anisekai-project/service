package me.anisekai.toshiko.data;

import me.anisekai.toshiko.enums.TorrentStatus;
import org.json.JSONObject;

public class RpcTorrent {

    private final int           id;
    private final TorrentStatus status;
    private final String        downloadDir;
    private final double        percentDone;

    public RpcTorrent(JSONObject source) {

        this.id          = source.getInt("id");
        this.status      = TorrentStatus.from(source.getInt("status"));
        this.downloadDir = source.getString("downloadDir");
        this.percentDone = source.getDouble("percentDone");
    }

    public int getId() {

        return this.id;
    }

    public TorrentStatus getStatus() {

        return this.status;
    }

    public String getDownloadDir() {

        return this.downloadDir;
    }

    public double getPercentDone() {

        return this.percentDone;
    }

}
