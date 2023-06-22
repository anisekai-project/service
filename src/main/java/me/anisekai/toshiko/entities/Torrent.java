package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import me.anisekai.toshiko.data.RpcTorrent;
import me.anisekai.toshiko.enums.TorrentStatus;

@Entity
public class Torrent {

    @Id
    private int id;

    @ManyToOne(optional = false)
    private Anime anime;

    @Column
    private String link;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TorrentStatus status;

    @Column
    private String downloadDir;

    @Column(nullable = false)
    private double percentDone;

    @Column(nullable = false)
    private String infoHash;

    public Torrent() {

    }

    public Torrent(Anime anime, int id, String name, String infoHash, String link) {

        this.id          = id;
        this.anime       = anime;
        this.link        = link;
        this.name        = name;
        this.status      = TorrentStatus.VERIFY_QUEUED;
        this.downloadDir = null;
        this.percentDone = 0;
        this.infoHash    = infoHash;
    }

    public void update(RpcTorrent torrent) {

        this.status      = torrent.getStatus();
        this.downloadDir = torrent.getDownloadDir();
        this.percentDone = torrent.getPercentDone();
    }

    public int getId() {

        return this.id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public Anime getAnime() {

        return this.anime;
    }

    public void setAnime(Anime anime) {

        this.anime = anime;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getLink() {

        return this.link;
    }

    public void setLink(String link) {

        this.link = link;
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

    public String getInfoHash() {

        return this.infoHash;
    }

    public void setInfoHash(String infoHash) {

        this.infoHash = infoHash;
    }
}
