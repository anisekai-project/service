package me.anisekai.api.transmission;

import me.anisekai.api.json.BookshelfJson;

public class TransmissionTorrent {

    private final String        hash;
    private final TorrentStatus status;
    private final String        downloadDir;
    private final double        percentDone;
    private final String        file;

    public TransmissionTorrent(BookshelfJson data) {

        this.hash        = data.getString("hashString");
        this.status      = TorrentStatus.from(data.getInt("status"));
        this.downloadDir = data.getString("downloadDir");
        this.percentDone = data.getDouble("percentDone");
        this.file        = data.getString("files.0.name");
    }

    public String getHash() {

        return this.hash;
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

    public String getFile() {

        return this.file;
    }

}
