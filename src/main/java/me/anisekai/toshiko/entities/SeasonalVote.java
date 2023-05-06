package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import me.anisekai.toshiko.entities.keys.UserAnimeAssocKey;

@Entity
@IdClass(UserAnimeAssocKey.class)
public class SeasonalVote {

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private DiscordUser user;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Anime anime;

    public SeasonalVote() {

    }

    public SeasonalVote(DiscordUser user, Anime anime) {

        this.user  = user;
        this.anime = anime;
    }

    public DiscordUser getUser() {

        return this.user;
    }

    public Anime getAnime() {

        return this.anime;
    }
}
