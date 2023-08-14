package me.anisekai.toshiko.entities.keys;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class UserAnimeAssocKey implements Serializable {

    private Long anime;
    private Long user;

    public UserAnimeAssocKey() {}

    public UserAnimeAssocKey(@NotNull Long anime, @NotNull Long user) {

        this.anime = anime;
        this.user  = user;
    }

    public UserAnimeAssocKey(@NotNull Anime anime, @NotNull DiscordUser user) {

        this.anime = anime.getId();
        this.user  = user.getId();
    }

    public @NotNull Long getAnime() {

        return this.anime;
    }

    public @NotNull Long getUser() {

        return this.user;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserAnimeAssocKey that = (UserAnimeAssocKey) o;
        return Objects.equals(this.getAnime(), that.getAnime()) && Objects.equals(this.getUser(), that.getUser());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getAnime(), this.getUser());
    }

}
