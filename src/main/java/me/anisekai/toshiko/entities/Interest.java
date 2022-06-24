package me.anisekai.toshiko.entities;

import me.anisekai.toshiko.entities.keys.InterestKey;
import me.anisekai.toshiko.enums.InterestLevel;

import javax.persistence.*;
import java.util.Map;

@Entity
@IdClass(InterestKey.class)
public class Interest {

    @ManyToOne
    @Id
    private Anime anime;

    @ManyToOne
    @Id
    private DiscordUser user;

    @Enumerated(EnumType.STRING)
    private InterestLevel level;

    public Interest() {}

    public Interest(Anime anime, DiscordUser user, InterestLevel level) {

        this.anime = anime;
        this.user  = user;
        this.level = level;
    }

    public Anime getAnime() {

        return this.anime;
    }

    public DiscordUser getUser() {

        return this.user;
    }

    public InterestLevel getLevel() {

        return this.level;
    }

    public void setLevel(InterestLevel level) {

        this.level = level;
    }

    public double getValue(Map<DiscordUser, Double> powerMap) {

        return powerMap.getOrDefault(this.user, 0.0) * this.level.getPowerModifier();
    }

}
