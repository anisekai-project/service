package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import me.anisekai.toshiko.enums.TorrentStatus;
import me.anisekai.toshiko.interfaces.entities.ITorrent;
import me.anisekai.toshiko.utils.EntityUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

// TODO: Find a better way to store torrent reference to avoid id mix-up
@Entity
public class Torrent implements ITorrent {

    // <editor-fold desc="Entity Structure">

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

    @Column
    private String file;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    // </editor-fold>

    /**
     * As {@link ITorrent} primary key depend on an external service even when not persisted, this field is used to keep
     * track of this entity instance state.
     */
    @Transient
    private transient boolean created = true;

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
        this.file        = null;
    }

    // <editor-fold desc="Getters / Setters">

    @Override
    public Integer getId() {

        return this.id;
    }

    public void setId(Integer id) {

        this.id = id;
    }

    @Override
    public Anime getAnime() {

        return this.anime;
    }

    @Override
    public void setAnime(Anime anime) {

        this.anime = anime;
    }

    @Override
    public String getLink() {

        return this.link;
    }

    @Override
    public void setLink(String link) {

        this.link = link;
    }

    @Override
    public String getName() {

        return this.name;
    }

    @Override
    public void setName(String name) {

        this.name = name;
    }

    @Override
    public TorrentStatus getStatus() {

        return this.status;
    }

    @Override
    public void setStatus(TorrentStatus status) {

        this.status = status;
    }

    @Override
    public String getDownloadDir() {

        return this.downloadDir;
    }

    @Override
    public void setDownloadDir(String downloadDir) {

        this.downloadDir = downloadDir;
    }

    @Override
    public double getPercentDone() {

        return this.percentDone;
    }

    @Override
    public void setPercentDone(double percentDone) {

        this.percentDone = percentDone;
    }

    @Override
    public String getInfoHash() {

        return this.infoHash;
    }

    @Override
    public void setInfoHash(String infoHash) {

        this.infoHash = infoHash;
    }

    @Override
    public String getFile() {

        return this.file;
    }

    @Override
    public void setFile(String file) {

        this.file = file;
    }

    @Override
    public ZonedDateTime getCreatedAt() {

        return this.createdAt;
    }

    @Override
    public void setCreatedAt(ZonedDateTime createdAt) {

        this.createdAt = createdAt;
    }

    @Override
    public ZonedDateTime getUpdatedAt() {

        return this.updatedAt;
    }

    @Override
    public void setUpdatedAt(ZonedDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }

    // </editor-fold>


    @Override
    public boolean isNew() {

        return this.created;
    }

    @Override
    public boolean equals(Object o) {

        return o instanceof ITorrent other && EntityUtils.equals(this, other);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.id);
    }

    @PostLoad
    @PostPersist
    private void persisted() {

        this.created   = false;
        this.createdAt = this.createdAt.withZoneSameInstant(ZoneId.systemDefault());
        this.updatedAt = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

}
