package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import me.anisekai.toshiko.entities.keys.UserAnimeVoteAssocKey;

import java.util.Objects;

@Entity
@IdClass(UserAnimeVoteAssocKey.class)
public class SeasonalVote {

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private SeasonalSelection seasonalSelection;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private DiscordUser user;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Anime anime;

    public SeasonalVote() {

    }

    public SeasonalVote(SeasonalSelection seasonalSelection, DiscordUser user, Anime anime) {

        this.seasonalSelection = seasonalSelection;
        this.user              = user;
        this.anime             = anime;
    }

    public SeasonalSelection getSeasonalSelection() {

        return this.seasonalSelection;
    }

    public DiscordUser getUser() {

        return this.user;
    }

    public Anime getAnime() {

        return this.anime;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        SeasonalVote vote = (SeasonalVote) o;
        return Objects.equals(this.getSeasonalSelection(), vote.getSeasonalSelection()) && Objects.equals(this.getUser(), vote.getUser()) && Objects.equals(this.getAnime(), vote.getAnime());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getSeasonalSelection(), this.getUser(), this.getAnime());
    }
}
