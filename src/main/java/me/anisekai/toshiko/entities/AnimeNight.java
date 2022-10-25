package me.anisekai.toshiko.entities;

import me.anisekai.toshiko.enums.ScheduledEventState;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
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

        this.id     = scheduledEvent.getIdLong();
        this.anime  = anime;
        this.amount = amount;
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
