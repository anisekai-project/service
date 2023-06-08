package me.anisekai.toshiko.entities.keys;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.SeasonalSelection;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class UserAnimeVoteAssocKey implements Serializable {

    private Long seasonalSelection;
    private Long anime;
    private Long user;

    public UserAnimeVoteAssocKey() {}

    public UserAnimeVoteAssocKey(@NotNull Long seasonalSelection, @NotNull Long anime, @NotNull Long user) {

        this.seasonalSelection = seasonalSelection;
        this.anime             = anime;
        this.user              = user;
    }

    public UserAnimeVoteAssocKey(@NotNull SeasonalSelection seasonalSelection, @NotNull Anime anime, @NotNull DiscordUser user) {

        this.seasonalSelection = seasonalSelection.getId();
        this.anime = anime.getId();
        this.user  = user.getId();
    }

    public @NotNull Long getSeasonalSelection() {

        return this.seasonalSelection;
    }

    public @NotNull Long getAnime() {

        return this.anime;
    }

    public @NotNull Long getUser() {

        return this.user;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        UserAnimeVoteAssocKey that = (UserAnimeVoteAssocKey) o;
        return Objects.equals(this.getSeasonalSelection(), that.getSeasonalSelection()) && Objects.equals(this.getAnime(), that.getAnime()) && Objects.equals(this.getUser(), that.getUser());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getSeasonalSelection(), this.getAnime(), this.getUser());
    }
}
