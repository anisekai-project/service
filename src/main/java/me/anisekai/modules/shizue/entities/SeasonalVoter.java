package me.anisekai.modules.shizue.entities;

import jakarta.persistence.*;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.shizue.entities.keys.SeasonalVoterKey;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalVoter;
import me.anisekai.api.persistence.EntityUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@IdClass(SeasonalVoterKey.class)
public class SeasonalVoter implements ISeasonalVoter {

    // <editor-fold desc="Entity Structure">

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private SeasonalSelection seasonalSelection;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private DiscordUser user;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    // </editor-fold>

    @Transient
    private boolean created = true;

    public SeasonalVoter() {

    }

    public SeasonalVoter(SeasonalSelection seasonalSelection, DiscordUser user) {

        this(seasonalSelection, user, 0);
    }

    public SeasonalVoter(SeasonalSelection seasonalSelection, DiscordUser user, int amount) {

        this.seasonalSelection = seasonalSelection;
        this.user              = user;
        this.amount            = amount;
    }

    // <editor-fold desc="Getters / Setters">

    @Override
    public SeasonalVoterKey getId() {

        return new SeasonalVoterKey(this.seasonalSelection, this.user);
    }

    @Override
    public void setId(SeasonalVoterKey id) {

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
    public int getAmount() {

        return this.amount;
    }

    @Override
    public void setAmount(int amount) {

        this.amount = amount;
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

        return o instanceof ISeasonalVoter other && EntityUtils.equals(this, other);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getSeasonalSelection(), this.getUser(), this.getAmount());
    }

    @PostLoad
    @PostPersist
    private void persisted() {

        this.created   = false;
        this.createdAt = this.createdAt.withZoneSameInstant(ZoneId.systemDefault());
        this.updatedAt = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

}
