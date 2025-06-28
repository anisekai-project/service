package fr.anisekai.server.entities;

import fr.anisekai.wireless.remote.enums.BroadcastStatus;
import fr.anisekai.wireless.remote.interfaces.BroadcastEntity;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.*;
import fr.anisekai.server.entities.adapters.BroadcastEventAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Broadcast implements BroadcastEventAdapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Anime watchTarget;

    @Column(nullable = false)
    private ZonedDateTime startingAt;

    @Column
    private Long eventId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BroadcastStatus status;

    @Column(nullable = false)
    private int episodeCount;

    @Column(nullable = false)
    private int firstEpisode;

    @Column(nullable = false)
    private boolean skipEnabled;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public Long getId() {

        return this.id;
    }

    @Override
    public @NotNull Anime getWatchTarget() {

        return this.watchTarget;
    }

    @Override
    public void setWatchTarget(@NotNull Anime watchTarget) {

        this.watchTarget = watchTarget;
    }

    @Override
    public @NotNull ZonedDateTime getStartingAt() {

        return this.startingAt;
    }

    @Override
    public void setStartingAt(@NotNull ZonedDateTime startingAt) {

        this.startingAt = startingAt;
    }

    @Override
    public @Nullable Long getEventId() {

        return this.eventId;
    }

    @Override
    public void setEventId(Long eventId) {

        this.eventId = eventId;
    }

    @Override
    public @NotNull BroadcastStatus getStatus() {

        return this.status;
    }

    @Override
    public void setStatus(@NotNull BroadcastStatus status) {

        this.status = status;
    }

    @Override
    public int getEpisodeCount() {

        return this.episodeCount;
    }

    @Override
    public void setEpisodeCount(int episodeCount) {

        this.episodeCount = episodeCount;
    }

    @Override
    public int getFirstEpisode() {

        return this.firstEpisode;
    }

    @Override
    public void setFirstEpisode(int firstEpisode) {

        this.firstEpisode = firstEpisode;
    }

    @Override
    public boolean isSkipEnabled() {

        return this.skipEnabled;
    }

    @Override
    public void setSkipEnabled(boolean skipEnabled) {

        this.skipEnabled = skipEnabled;
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

        if (o instanceof BroadcastEntity<?> broadcast) return EntityUtils.equals(this, broadcast);
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
