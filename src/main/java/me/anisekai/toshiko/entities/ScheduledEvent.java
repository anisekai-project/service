package me.anisekai.toshiko.entities;

import me.anisekai.toshiko.enums.ScheduledEventState;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class ScheduledEvent implements Comparable<ScheduledEvent> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, unique = true)
    private LocalDateTime eventStartAt;

    @ManyToOne
    private Anime anime;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int lastEpisode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ScheduledEventState state;

    @Column(nullable = false)
    private boolean notified;

    public ScheduledEvent() {}

    /**
     * Create a new {@link ScheduledEvent}.
     *
     * @param anime
     *         The {@link Anime} to associate to this {@link ScheduledEvent}.
     * @param eventStartAt
     *         The {@link LocalDateTime} at which this {@link ScheduledEvent} should start.
     * @param description
     *          This {@link ScheduledEvent} description.
     * @param lastEpisode
     *         The last episode that will be watched during the {@link ScheduledEvent}.
     */
    public ScheduledEvent(@NotNull Anime anime, @NotNull LocalDateTime eventStartAt, @NotNull String description, int lastEpisode) {

        this.eventStartAt = eventStartAt;
        this.anime        = anime;
        this.description  = description;
        this.lastEpisode  = lastEpisode;
        this.notified     = false;
        this.state        = ScheduledEventState.SCHEDULED;

    }

    public Integer getId() {

        return this.id;
    }

    public @NotNull LocalDateTime getEventStartAt() {

        return this.eventStartAt;
    }

    public void setEventStartAt(@NotNull LocalDateTime eventStartAt) {

        this.eventStartAt = eventStartAt;
    }

    public @NotNull Anime getAnime() {

        return this.anime;
    }

    public void setAnime(@NotNull Anime anime) {

        this.anime = anime;
    }

    public @NotNull String getDescription() {

        return this.description;
    }

    public void setDescription(@NotNull String description) {

        this.description = description;
    }

    public int getLastEpisode() {

        return this.lastEpisode;
    }

    public void setLastEpisode(int lastEpisode) {

        this.lastEpisode = lastEpisode;
    }

    public ScheduledEventState getState() {

        return this.state;
    }

    public void setState(ScheduledEventState state) {

        this.state = state;
    }

    public boolean isNotified() {

        return this.notified;
    }

    public void setNotified(boolean notified) {

        this.notified = notified;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ScheduledEvent that = (ScheduledEvent) o;
        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId());
    }

    @Override
    public int compareTo(@NotNull ScheduledEvent other) {

        return this.getEventStartAt().compareTo(other.getEventStartAt());
    }
}
