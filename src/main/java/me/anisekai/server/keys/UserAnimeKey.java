package me.anisekai.server.keys;

import me.anisekai.server.interfaces.IAnime;
import me.anisekai.server.interfaces.IDiscordUser;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class UserAnimeKey implements Serializable {

    private Long user;
    private Long anime;

    public UserAnimeKey() {}

    public UserAnimeKey(@NotNull Long anime, @NotNull Long user) {

        this.anime = anime;
        this.user  = user;
    }

    public static UserAnimeKey of(IAnime<?> anime, IDiscordUser user) {

        if (anime == null || user == null) return null;
        return new UserAnimeKey(anime.getId(), user.getId());
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
        UserAnimeKey that = (UserAnimeKey) o;
        return Objects.equals(this.getAnime(), that.getAnime()) && Objects.equals(this.getUser(), that.getUser());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getAnime(), this.getUser());
    }

}
