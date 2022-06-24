package me.anisekai.toshiko.entities.keys;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;

import java.io.Serializable;

public class InterestKey implements Serializable {

    private Long anime;
    private Long user;

    public InterestKey() {

    }

    public InterestKey(Long anime, Long user) {

        this.anime = anime;
        this.user  = user;
    }

    public InterestKey(Anime anime, DiscordUser user) {

        this.anime = anime.getId();
        this.user  = user.getId();
    }

    public Long getAnime() {

        return this.anime;
    }

    public Long getUser() {

        return this.user;
    }

}
