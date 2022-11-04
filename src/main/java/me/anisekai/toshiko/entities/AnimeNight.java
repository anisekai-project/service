package me.anisekai.toshiko.entities;

import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
public class AnimeNight {

    @Id
    @Column(nullable = false)
    private long id;

    @ManyToOne
    private Anime anime;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private ScheduledEvent.Status status;

    @Nullable
    private String imageUrl;

    @Column(nullable = false)
    @NotNull
    private OffsetDateTime startTime;

    @Column(nullable = false)
    @NotNull
    private OffsetDateTime endTime;

    public AnimeNight() {}

    /**
     * Create a new {@link AnimeNight}.
     *
     * @param anime
     *         The {@link Anime} to associate to this {@link AnimeNight}.
     * @param amount
     *         The amount of episode that will be watched during the {@link AnimeNight}.
     */
    public AnimeNight(ScheduledEvent scheduledEvent, @NotNull Anime anime, long amount) {

        this.id        = scheduledEvent.getIdLong();
        this.anime     = anime;
        this.amount    = amount;
        this.status    = scheduledEvent.getStatus();
        this.imageUrl  = scheduledEvent.getImageUrl();
        this.startTime = scheduledEvent.getStartTime();
        this.endTime   = Objects.requireNonNull(scheduledEvent.getEndTime());
    }

    public long getId() {

        return this.id;
    }

    public Anime getAnime() {

        return this.anime;
    }

    public void setAnime(Anime anime) {

        this.anime = anime;
    }

    public long getAmount() {

        return this.amount;
    }

    public void setAmount(long amount) {

        this.amount = amount;
    }

    public ScheduledEvent.@NotNull Status getStatus() {

        return this.status;
    }

    public void setStatus(ScheduledEvent.@NotNull Status status) {

        this.status = status;
    }

    public @Nullable String getImageUrl() {

        return this.imageUrl;
    }

    public void setImageUrl(@Nullable String imageUrl) {

        this.imageUrl = imageUrl;
    }

    public @NotNull OffsetDateTime getStartTime() {

        return this.startTime;
    }

    public void setStartTime(@NotNull OffsetDateTime startTime) {

        this.startTime = startTime;
    }

    public @NotNull OffsetDateTime getEndTime() {

        return this.endTime;
    }

    public void setEndTime(@NotNull OffsetDateTime endTime) {

        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        //noinspection ChainOfInstanceofChecks
        if (o instanceof AnimeNight other) {
            return other.getId() == this.getId();
        } else if (o instanceof ScheduledEvent other) {
            return other.getIdLong() == this.getId();
        }
        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId());
    }
}
