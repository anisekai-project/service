package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import me.anisekai.toshiko.entities.keys.UserAnimeAssocKey;
import me.anisekai.toshiko.enums.InterestLevel;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

@Entity
@IdClass(UserAnimeAssocKey.class)
public class Interest {

    @Id
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Anime anime;

    @Id
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
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

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Interest interest = (Interest) o;
        return Objects.equals(this.getAnime(), interest.getAnime()) && Objects.equals(
                this.getUser(),
                interest.getUser()
        );
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getAnime(), this.getUser());
    }

}
