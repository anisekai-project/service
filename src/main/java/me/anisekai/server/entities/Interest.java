package me.anisekai.server.entities;

import jakarta.persistence.*;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.server.interfaces.IInterest;
import me.anisekai.server.keys.UserAnimeKey;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@IdClass(UserAnimeKey.class)
public class Interest implements IInterest<DiscordUser, Anime> {

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private DiscordUser user;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Anime anime;

    @Column(nullable = false)
    private long level; // From -2 to 2 (0 being neutral)

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public DiscordUser getUser() {

        return this.user;
    }

    @Override
    public void setUser(DiscordUser user) {

        this.user = user;
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
    public long getLevel() {

        return this.level;
    }

    @Override
    public void setLevel(long level) {

        this.level = level;
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

        if (o instanceof IInterest<?, ?> interest) return EntityUtils.equals(this, interest);
        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId());
    }

    @PreUpdate
    public void beforeSave() {

        this.updatedAt = ZonedDateTime.now();
    }

}
