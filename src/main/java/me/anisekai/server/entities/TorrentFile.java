package me.anisekai.server.entities;

import fr.anisekai.wireless.remote.interfaces.TorrentFileEntity;
import fr.anisekai.wireless.remote.keys.TorrentKey;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.*;
import me.anisekai.server.entities.adapters.TorrentFileEventAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@IdClass(TorrentKey.class)
public class TorrentFile implements TorrentFileEventAdapter {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    private Torrent torrent;

    @Id
    @Column(nullable = false)
    private int index;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Episode episode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public @NotNull Torrent getTorrent() {

        return this.torrent;
    }

    @Override
    public void setTorrent(@NotNull Torrent torrent) {

        this.torrent = torrent;
    }

    @Override
    public int getIndex() {

        return this.index;
    }

    @Override
    public void setIndex(int index) {

        this.index = index;
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
    public @NotNull String getName() {

        return this.name;
    }

    @Override
    public void setName(@NotNull String name) {

        this.name = name;
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

        if (o instanceof TorrentFileEntity<?, ?> torrentFile) return EntityUtils.equals(this, torrentFile);
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
