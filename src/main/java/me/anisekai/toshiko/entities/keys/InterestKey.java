package me.anisekai.toshiko.entities.keys;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class InterestKey implements Serializable {

    private Long anime;
    private Long user;

    public InterestKey() {}

    public InterestKey(@NotNull Long anime, @NotNull Long user) {

        this.anime = anime;
        this.user  = user;
    }

    public InterestKey(@NotNull Anime anime, @NotNull DiscordUser user) {

        this.anime = anime.getId();
        this.user  = user.getId();
    }

    public @NotNull Long getAnime() {

        return this.anime;
    }

    public @NotNull Long getUser() {

        return this.user;
    }

}
