package me.anisekai.server.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.server.enums.BroadcastStatus;
import me.anisekai.server.interfaces.IBroadcast;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Broadcast implements IBroadcast<Anime> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Anime watchTarget;

    @Column
    private @Nullable Long eventId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BroadcastStatus status;

    @Column(nullable = false)
    private long episodeCount;

    @Column(nullable = false)
    private long firstEpisode;

    @Column(nullable = false)
    private ZonedDateTime startingAt;

    @Column(nullable = false)
    private boolean skipEnabled;

    @Column(nullable = false)
    private boolean doProgress;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public Long getId() {

        return this.id;
    }

    @Override
    public Anime getWatchTarget() {

        return this.watchTarget;
    }

    @Override
    public void setWatchTarget(Anime watchTarget) {

        this.watchTarget = watchTarget;
    }

    @Override
    @Nullable
    public Long getEventId() {

        return this.eventId;
    }

    @Override
    public void setEventId(@Nullable Long eventId) {

        this.eventId = eventId;
    }

    @Override
    public BroadcastStatus getStatus() {

        return this.status;
    }

    @Override
    public void setStatus(BroadcastStatus status) {

        this.status = status;
    }

    @Override
    public long getEpisodeCount() {

        return this.episodeCount;
    }

    @Override
    public void setEpisodeCount(long episodeCount) {

        this.episodeCount = episodeCount;
    }

    @Override
    public long getFirstEpisode() {

        return this.firstEpisode;
    }

    @Override
    public void setFirstEpisode(long firstEpisode) {

        this.firstEpisode = firstEpisode;
    }

    @Override
    public ZonedDateTime getStartingAt() {

        return this.startingAt;
    }

    @Override
    public void setStartingAt(ZonedDateTime startingAt) {

        this.startingAt = startingAt;
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
    public boolean shouldDoProgress() {

        return this.doProgress;
    }

    @Override
    public void setDoProgress(boolean shouldDoProgress) {

        this.doProgress = shouldDoProgress;
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

        if (o instanceof IBroadcast<?> broadcast) return EntityUtils.equals(this, broadcast);
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
