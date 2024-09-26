package me.anisekai.modules.shizue.entities;

import jakarta.persistence.*;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.interfaces.AnimeNightMeta;
import me.anisekai.modules.shizue.interfaces.entities.IBroadcast;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Broadcast implements IBroadcast, AnimeNightMeta {

    // <editor-fold desc="Entity Structure">

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long eventId;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Anime anime;

    @Column(nullable = false)
    private long episodeCount;

    @Column(nullable = false)
    private long firstEpisode;

    @Column
    @Enumerated(EnumType.STRING)
    private ScheduledEvent.Status status;

    private String imageUrl;

    @Column(nullable = false)
    private ZonedDateTime startingAt;

    /**
     * This value cannot be written to or read by in the code, nor should it be. It is merely used as visual indicator
     * in the database as the event duration is not stored too.
     */
    @Column(nullable = false)
    private ZonedDateTime endingAt;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    // </editor-fold>

    public Broadcast() {}

    // <editor-fold desc="Getters / Setters">

    @Override
    public Long getId() {

        return this.id;
    }

    @Override
    public void setId(Long id) {

        this.id = id;
    }

    @Override
    public Long getEventId() {

        return this.eventId;
    }

    @Override
    public void setEventId(Long eventId) {

        this.eventId = eventId;
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

    @Override
    public Anime getWatchTarget() {

        return this.getAnime();
    }

    @Override
    public void setWatchTarget(Anime anime) {
        this.anime = anime;
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
    public ScheduledEvent.Status getStatus() {

        return this.status;
    }

    @Override
    public void setStatus(ScheduledEvent.Status status) {

        this.status = status;
    }

    @Override
    public String getImageUrl() {

        return this.imageUrl;
    }

    @Override
    public void setImageUrl(String imageUrl) {

        this.imageUrl = imageUrl;
    }

    @Override
    public ZonedDateTime getStartingAt() {

        return this.startingAt;
    }

    @Override
    public void setStartingAt(ZonedDateTime time) {

        this.startingAt = time;
    }

    @Override
    public boolean isSkipEnabled() {

        return true; // for now, it won't change the previous behavior.
    }

    @Override
    public void setSkipEnabled(boolean skipEnabled) {

        // NO-OP
    }

    public Anime getAnime() {

        return this.anime;
    }

    public void setAnime(Anime anime) {

        this.anime = anime;
    }
    // </editor-fold>

    @Override
    @Transient
    public boolean isNew() {

        return this.id == null;
    }

    @Override
    public boolean equals(Object o) {

        return o instanceof IBroadcast other && EntityUtils.equals(this, other);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.id);
    }

    @PrePersist
    private void updated() {

        // Ensure our value in database is up-to-date.
        this.endingAt = this.startingAt.plus(this.getDuration());
        this.setUpdatedAt(ZonedDateTime.now());
    }

    @Override
    public int compareTo(@NotNull AnimeNightMeta other) {

        return this.getStartingAt().compareTo(other.getStartingAt());
    }

    @PostLoad
    public void onLoad() {

        this.startingAt = this.startingAt.withZoneSameInstant(ZoneId.systemDefault());
        this.endingAt   = this.startingAt.plus(this.getDuration());
        this.createdAt  = this.createdAt.withZoneSameInstant(ZoneId.systemDefault());
        this.updatedAt  = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

}
