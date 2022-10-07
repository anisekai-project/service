package me.anisekai.toshiko.entities;

import me.anisekai.toshiko.entities.keys.InterestKey;
import me.anisekai.toshiko.enums.InterestLevel;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Map;

@Entity
@IdClass(InterestKey.class)
public class Interest {

    @Id
    @ManyToOne(optional = false)
    private Anime anime;

    @Id
    @ManyToOne(optional = false)
    private DiscordUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestLevel level;

    public Interest() {}

    public Interest(@NotNull Anime anime, @NotNull DiscordUser user) {

        this(anime, user, InterestLevel.NEUTRAL);
    }

    public Interest(@NotNull Anime anime, @NotNull DiscordUser user, @NotNull InterestLevel level) {

        this.anime = anime;
        this.user  = user;
        this.level = level;
    }

    public @NotNull Anime getAnime() {

        return this.anime;
    }

    public @NotNull DiscordUser getUser() {

        return this.user;
    }

    public @NotNull InterestLevel getLevel() {

        return this.level;
    }

    public void setLevel(@NotNull InterestLevel level) {

        this.level = level;
    }

    public double getValue(Map<DiscordUser, Double> powerMap) {
        return powerMap.getOrDefault(this.getUser(), 0.0) * this.getLevel().getPowerModifier();
    }
}
