package me.anisekai.server.entities;

import fr.anisekai.wireless.remote.enums.AnimeList;
import fr.anisekai.wireless.remote.interfaces.WatchlistEntity;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.*;
import me.anisekai.server.entities.adapters.WatchlistEventAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class Watchlist implements WatchlistEventAdapter {

    @Id
    @Enumerated(EnumType.STRING)
    private AnimeList id;

    @Column
    private Long messageId;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Anime> animes;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public AnimeList getId() {

        return this.id;
    }

    @Override
    public void setId(AnimeList id) {

        this.id = id;
    }

    @Override
    public @Nullable Long getMessageId() {

        return this.messageId;
    }

    @Override
    public void setMessageId(@Nullable Long messageId) {

        this.messageId = messageId;
    }

    @Override
    public @NotNull Set<Anime> getAnimes() {

        return this.animes;
    }

    @Override
    public void setAnimes(@NotNull Set<Anime> animes) {

        this.animes = animes;
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

        if (o instanceof WatchlistEntity<?> watchlist) return EntityUtils.equals(this, watchlist);
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
