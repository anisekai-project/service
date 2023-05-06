package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import me.anisekai.toshiko.entities.keys.SeasonalVoterKey;

@Entity
@IdClass(SeasonalVoterKey.class)
public class SeasonalVoter {

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private SeasonalSelection seasonalSelection;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private DiscordUser user;

    @Column(nullable = false)
    private int amount;

    public SeasonalVoter() {

    }

    public SeasonalVoter(SeasonalSelection seasonalSelection, DiscordUser user) {

        this(seasonalSelection, user, 1);
    }

    public SeasonalVoter(SeasonalSelection seasonalSelection, DiscordUser user, int amount) {

        this.seasonalSelection = seasonalSelection;
        this.user              = user;
        this.amount            = amount;
    }

    public SeasonalSelection getSeasonalSelection() {

        return this.seasonalSelection;
    }

    public DiscordUser getUser() {

        return this.user;
    }

    public int getAmount() {

        return this.amount;
    }

    public void setAmount(int amount) {

        this.amount = amount;
    }
}
