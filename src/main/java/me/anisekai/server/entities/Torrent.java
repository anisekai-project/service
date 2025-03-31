package me.anisekai.server.entities;

import jakarta.persistence.*;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.api.transmission.TorrentStatus;
import me.anisekai.server.interfaces.ITorrent;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Torrent implements ITorrent<Episode> {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.EAGER)
    private Episode episode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TorrentStatus status = TorrentStatus.DOWNLOAD_QUEUED;

    @Column(nullable = false)
    private double progress = 0;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private String downloadDirectory;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private int priority = 0;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public String getId() {

        return this.id;
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
    public Episode getEpisode() {

        return this.episode;
    }

    @Override
    public void setEpisode(Episode episode) {

        this.episode = episode;
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
    public double getProgress() {

        return this.progress;
    }

    @Override
    public void setProgress(double progress) {

        this.progress = progress;
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
    public String getDownloadDirectory() {

        return this.downloadDirectory;
    }

    @Override
    public void setDownloadDirectory(String downloadDirectory) {

        this.downloadDirectory = downloadDirectory;
    }

    @Override
    public String getFileName() {

        return this.fileName;
    }

    @Override
    public void setFileName(String fileName) {

        this.fileName = fileName;
    }

    @Override
    public long getPriority() {

        return this.priority;
    }

    @Override
    public void setPriority(long priority) {

        this.priority = (int) priority;
    }

    @Override
    public ZonedDateTime getCreatedAt() {

        return this.createdAt;
    }

    @Override
    public ZonedDateTime getUpdatedAt() {

        return this.updatedAt;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof ITorrent<?> torrent) return EntityUtils.equals(this, torrent);
        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(this.getId());
    }

    @PreUpdate
    public void beforeSave() {

        this.updatedAt = ZonedDateTime.now();
    }

}
