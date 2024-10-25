package me.anisekai.modules.shizue.entities;

import jakarta.persistence.*;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.entities.keys.UserAnimeAssocKey;
import me.anisekai.modules.shizue.enums.InterestLevel;
import me.anisekai.modules.shizue.interfaces.entities.IInterest;
import me.anisekai.api.persistence.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@IdClass(UserAnimeAssocKey.class)
public class Interest implements IInterest {

    // <editor-fold desc="Entity Structure">

    @Id
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Anime anime;

    @Id
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private DiscordUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestLevel level;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    // </editor-fold>

    /**
     * As {@link IInterest} primary key is a composite key with defined value even when not persisted, this field is
     * used to keep track of this entity instance state.
     */
    @Transient
    private transient boolean created = true;

    public Interest() {}

    public Interest(@NotNull Anime anime, @NotNull DiscordUser user) {

        this(anime, user, InterestLevel.NEUTRAL);
    }

    public Interest(@NotNull Anime anime, @NotNull DiscordUser user, @NotNull InterestLevel level) {

        this.anime = anime;
        this.user  = user;
        this.level = level;
    }

    // <editor-fold desc="Getters / Setters">

    @Override
    public UserAnimeAssocKey getId() {

        return new UserAnimeAssocKey(this.anime, this.user);
    }

    @Override
    public void setId(UserAnimeAssocKey id) {

        throw new UnsupportedOperationException("Cannot set a composite key.");
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
    public DiscordUser getUser() {

        return this.user;
    }

    @Override
    public void setUser(DiscordUser user) {

        this.user = user;
    }

    @Override
    public InterestLevel getLevel() {

        return this.level;
    }

    @Override
    public void setLevel(InterestLevel level) {

        this.level = level;
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

    // </editor-fold>

    @Override
    public boolean isNew() {

        return this.created;
    }

    @Override
    public boolean equals(Object o) {

        return o instanceof IInterest other && EntityUtils.equals(this, other);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getAnime(), this.getUser());
    }

    @PostLoad
    @PostPersist
    private void persisted() {

        this.created   = false;
        this.createdAt = this.createdAt.withZoneSameInstant(ZoneId.systemDefault());
        this.updatedAt = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

}
