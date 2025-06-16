package fr.anisekai.server.entities;

import fr.anisekai.wireless.api.services.Transmission;
import fr.anisekai.wireless.remote.interfaces.TorrentEntity;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.*;
import fr.anisekai.server.entities.adapters.TorrentEventAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class Torrent implements TorrentEventAdapter {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Transmission.TorrentStatus status = Transmission.TorrentStatus.DOWNLOAD_QUEUED;

    @Column(nullable = false)
    private double progress = 0;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private String downloadDirectory;

    @Column(nullable = false)
    private byte priority;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "torrent")
    private Set<TorrentFile> files;

    @Override
    public String getId() {

        return this.id;
    }

    @Override
    public void setId(String id) {

        this.id = id;
    }

    @Override
    public @NotNull String getName() {

        return this.name;
    }

    @Override
    public void setName(@NotNull String name) {

        this.name = name;
    }

    @Override
    public @NotNull Transmission.TorrentStatus getStatus() {

        return this.status;
    }

    @Override
    public void setStatus(Transmission.@NotNull TorrentStatus status) {

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
    public @NotNull String getLink() {

        return this.link;
    }

    @Override
    public void setLink(@NotNull String link) {

        this.link = link;
    }

    @Override
    public @NotNull String getDownloadDirectory() {

        return this.downloadDirectory;
    }

    @Override
    public void setDownloadDirectory(@NotNull String downloadDirectory) {

        this.downloadDirectory = downloadDirectory;
    }

    @Override
    public byte getPriority() {

        return this.priority;
    }

    @Override
    public void setPriority(byte priority) {

        this.priority = priority;
    }

    @Override
    public ZonedDateTime getCreatedAt() {

        return this.createdAt;
    }

    @Override
    public ZonedDateTime getUpdatedAt() {

        return this.updatedAt;
    }

    public Set<TorrentFile> getFiles() {

        return this.files;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof TorrentEntity torrent) return EntityUtils.equals(this, torrent);
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

    public Transmission.Torrent asTransmissionIdentifier() {

        return new Transmission.Torrent(
                this.id,
                this.status,
                this.downloadDirectory,
                this.progress,
                this.getFiles().stream().map(TorrentFile::getName).toList()
        );
    }

}
