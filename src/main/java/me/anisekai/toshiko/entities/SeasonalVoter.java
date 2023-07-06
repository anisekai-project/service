package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import me.anisekai.toshiko.entities.keys.SeasonalVoterKey;

import java.util.Objects;

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

        this(seasonalSelection, user, 0);
    }

    public SeasonalVoter(SeasonalSelection seasonalSelection, DiscordUser user, int amount) {

        this.seasonalSelection = seasonalSelection;
        this.user              = user;
        this.amount            = amount;
    }

    public SeasonalSelection getSeasonalSelection() {

        return this.seasonalSelection;
    }

    public void setSeasonalSelection(SeasonalSelection seasonalSelection) {

        this.seasonalSelection = seasonalSelection;
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

    @Override
    public boolean equals(Object o) {

        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        SeasonalVoter voter = (SeasonalVoter) o;
        return this.getAmount() == voter.getAmount() && Objects.equals(
                this.getSeasonalSelection(),
                voter.getSeasonalSelection()
        ) && Objects.equals(this.getUser(), voter.getUser());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getSeasonalSelection(), this.getUser(), this.getAmount());
    }

}
