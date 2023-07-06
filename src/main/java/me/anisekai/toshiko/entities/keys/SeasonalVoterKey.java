package me.anisekai.toshiko.entities.keys;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.SeasonalSelection;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class SeasonalVoterKey implements Serializable {

    private Long seasonalSelection;
    private Long user;

    public SeasonalVoterKey() {

    }

    public SeasonalVoterKey(@NotNull Long seasonalSelection, @NotNull Long user) {

        this.seasonalSelection = seasonalSelection;
        this.user              = user;
    }

    public SeasonalVoterKey(@NotNull SeasonalSelection seasonalSelection, @NotNull DiscordUser user) {

        this.seasonalSelection = seasonalSelection.getId();
        this.user              = user.getId();
    }

    public @NotNull Long getSeasonalSelection() {

        return this.seasonalSelection;
    }

    public @NotNull Long getUser() {

        return this.user;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        SeasonalVoterKey that = (SeasonalVoterKey) o;
        return Objects.equals(
                this.getSeasonalSelection(),
                that.getSeasonalSelection()
        ) && Objects.equals(this.getUser(), that.getUser());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getSeasonalSelection(), this.getUser());
    }

}
