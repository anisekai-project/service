package me.anisekai.modules.shizue.entities;

import jakarta.persistence.*;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.interfaces.AnimeNightMeta;
import me.anisekai.modules.shizue.interfaces.entities.IBroadcast;
import me.anisekai.api.persistence.EntityUtils;
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
    private long amount;

    @Column(nullable = false)
    private long firstEpisode;

    @Column(nullable = false)
    private long lastEpisode;

    @Column
    @Enumerated(EnumType.STRING)
    private ScheduledEvent.Status status;

    private String imageUrl;

    @Column(nullable = false)
    private ZonedDateTime startDateTime;

    @Column(nullable = false)
    private ZonedDateTime endDateTime;

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
    public Anime getAnime() {

        return this.anime;
    }

    @Override
    public void setAnime(Anime anime) {

        this.anime = anime;
    }

    @Override
    public long getAmount() {

        return this.amount;
    }

    @Override
    public void setAmount(long amount) {

        this.amount = amount;
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
    public long getLastEpisode() {

        return this.lastEpisode;
    }

    @Override
    public void setLastEpisode(long lastEpisode) {

        this.lastEpisode = lastEpisode;
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
    public ZonedDateTime getStartDateTime() {

        return this.startDateTime;
    }

    @Override
    public void setStartDateTime(ZonedDateTime startDateTime) {

        this.startDateTime = startDateTime;
    }

    @Override
    public ZonedDateTime getEndDateTime() {

        return this.endDateTime;
    }

    @Override
    public void setEndDateTime(ZonedDateTime endDateTime) {

        this.endDateTime = endDateTime;
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

        this.setUpdatedAt(ZonedDateTime.now());
    }

    @Override
    public int compareTo(@NotNull AnimeNightMeta other) {

        return this.getStartDateTime().compareTo(other.getStartDateTime());
    }

    @PostLoad
    public void onLoad() {

        this.startDateTime = this.startDateTime.withZoneSameInstant(ZoneId.systemDefault());
        this.endDateTime   = this.endDateTime.withZoneSameInstant(ZoneId.systemDefault());
        this.createdAt     = this.createdAt.withZoneSameInstant(ZoneId.systemDefault());
        this.updatedAt     = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

}
