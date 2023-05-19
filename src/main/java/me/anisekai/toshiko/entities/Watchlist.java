package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.CronState;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

@Entity
public class Watchlist implements Comparable<Watchlist> {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnimeStatus status;

    @Column
    private Long messageId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "status")
    private Set<Anime> animes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CronState state;

    public Watchlist() {}

    public Watchlist(@NotNull AnimeStatus status) {

        this.status    = status;
        this.messageId = null;
        this.state     = CronState.DONE;
    }

    public @NotNull AnimeStatus getStatus() {

        return this.status;
    }

    public @Nullable Long getMessageId() {

        return this.messageId;
    }

    public void setMessageId(@Nullable Long messageId) {

        this.messageId = messageId;
    }

    public Set<Anime> getAnimes() {

        return this.animes;
    }

    public void setAnimes(Set<Anime> animes) {

        this.animes = animes;
    }

    public CronState getState() {

        return this.state;
    }

    public void setState(CronState state) {

        this.state = state;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Watchlist watchlist = (Watchlist) o;
        return this.getStatus() == watchlist.getStatus();
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getStatus());
    }

    @Override
    public int compareTo(@NotNull Watchlist o) {

        return this.getStatus().compareTo(o.getStatus());
    }
}
