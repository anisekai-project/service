package me.anisekai.toshiko.data;

import me.anisekai.toshiko.enums.TorrentStatus;
import org.json.JSONObject;

public class RpcTorrent {

    private int           id;
    private String        name;
    private TorrentStatus status;
    private String        downloadDir;
    private double        percentDone;

    public RpcTorrent() {

    }

    public RpcTorrent(JSONObject source) {

        this.id          = source.getInt("id");
        this.name        = source.getString("name");
        this.status      = TorrentStatus.from(source.getInt("status"));
        this.downloadDir = source.getString("downloadDir");
        this.percentDone = source.getDouble("percentDone");
    }

    public int getId() {

        return this.id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public TorrentStatus getStatus() {

        return this.status;
    }

    public void setStatus(TorrentStatus status) {

        this.status = status;
    }

    public String getDownloadDir() {

        return this.downloadDir;
    }

    public void setDownloadDir(String downloadDir) {

        this.downloadDir = downloadDir;
    }

    public double getPercentDone() {

        return this.percentDone;
    }

    public void setPercentDone(double percentDone) {

        this.percentDone = percentDone;
    }
}
