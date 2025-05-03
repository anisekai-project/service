package me.anisekai.server.entities;

import fr.anisekai.wireless.remote.interfaces.InterestEntity;
import fr.anisekai.wireless.remote.keys.InterestKey;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.*;
import me.anisekai.server.entities.adapters.InterestEventAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@IdClass(InterestKey.class)
public class Interest implements InterestEventAdapter {

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private DiscordUser user;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Anime anime;

    @Column(nullable = false)
    private byte level; // From -2 to 2 (0 being neutral)

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public @NotNull DiscordUser getUser() {

        return this.user;
    }

    @Override
    public void setUser(@NotNull DiscordUser user) {

        this.user = user;
    }

    @Override
    public @NotNull Anime getAnime() {

        return this.anime;
    }

    @Override
    public void setAnime(@NotNull Anime anime) {

        this.anime = anime;
    }

    @Override
    public byte getLevel() {

        return this.level;
    }

    @Override
    public void setLevel(byte level) {

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

        if (o instanceof InterestEntity<?, ?> interest) return EntityUtils.equals(this, interest);
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
