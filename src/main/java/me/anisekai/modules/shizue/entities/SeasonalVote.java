package me.anisekai.modules.shizue.entities;

import jakarta.persistence.*;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.entities.keys.UserAnimeVoteAssocKey;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalVote;
import me.anisekai.api.persistence.EntityUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@IdClass(UserAnimeVoteAssocKey.class)
public class SeasonalVote implements ISeasonalVote {

    // <editor-fold desc="Entity Structure">

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private SeasonalSelection seasonalSelection;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private DiscordUser user;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Anime anime;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    // </editor-fold>

    /**
     * As {@link ISeasonalVote} primary key is a composite key with defined value even when not persisted, this field is
     * used to keep track of this entity instance state.
     */
    @Transient
    private transient boolean created = false;

    public SeasonalVote() {}

    public SeasonalVote(SeasonalSelection seasonalSelection, DiscordUser user, Anime anime) {

        this.seasonalSelection = seasonalSelection;
        this.user              = user;
        this.anime             = anime;
    }

    // <editor-fold desc="Getters / Setters">

    @Override
    public UserAnimeVoteAssocKey getId() {

        return new UserAnimeVoteAssocKey(this.seasonalSelection, this.anime, this.user);
    }

    @Override
    public void setId(UserAnimeVoteAssocKey id) {

        throw new UnsupportedOperationException("Cannot set a composite key.");
    }

    @Override
    public SeasonalSelection getSeasonalSelection() {

        return this.seasonalSelection;
    }

    @Override
    public void setSeasonalSelection(SeasonalSelection seasonalSelection) {

        this.seasonalSelection = seasonalSelection;
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
    public Anime getAnime() {

        return this.anime;
    }

    @Override
    public void setAnime(Anime anime) {

        this.anime = anime;
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

        return o instanceof ISeasonalVote other && EntityUtils.equals(this, other);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getSeasonalSelection(), this.getUser(), this.getAnime());
    }

    @PostLoad
    @PostPersist
    private void persisted() {

        this.created   = false;
        this.createdAt = this.createdAt.withZoneSameInstant(ZoneId.systemDefault());
        this.updatedAt = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

}
