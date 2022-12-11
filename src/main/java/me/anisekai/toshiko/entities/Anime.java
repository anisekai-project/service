package me.anisekai.toshiko.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.interfaces.AnimeProvider;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class Anime implements Comparable<Anime> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob
    @Column(length = 32_768)
    private String synopsis;

    @Column
    private String genres;

    @Column
    private String themes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnimeStatus status;

    @OneToMany(mappedBy = "anime", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Interest> interests;

    @ManyToOne(optional = false)
    private DiscordUser addedBy;

    @Column(nullable = false, unique = true)
    private String link;

    @Column
    private String image;

    @Column(nullable = false)
    private long watched = 0;

    @Column(nullable = false)
    private long total = 0;

    @Column(nullable = false)
    private long episodeDuration = 0;

    @Column
    private Long announceMessage;

    @Column(nullable = false)
    private ZonedDateTime addedAt;

    public Anime() {}

    public Anime(@NotNull DiscordUser user, @NotNull AnimeProvider provider) {

        this(user, provider, provider.getPublicationState().getStatus());
    }

    public Anime(@NotNull DiscordUser user, @NotNull AnimeProvider provider, @NotNull AnimeStatus status) {

        this.name            = provider.getName();
        this.status          = status;
        this.addedBy         = user;
        this.image           = provider.getImage();
        this.link            = provider.getUrl();
        this.addedAt         = ZonedDateTime.now().withNano(0);
        this.total           = provider.getEpisodeCount().orElse(0L);
        this.episodeDuration = 24;
    }

    public void patch(Anime other) {

        this.synopsis        = other.getSynopsis();
        this.genres          = other.getGenres();
        this.themes          = other.getThemes();
        this.status          = other.getStatus();
        this.link            = other.getLink();
        this.image           = other.getImage();
        this.total           = other.getTotal();
        this.episodeDuration = other.getEpisodeDuration();
    }

    public Long getId() {

        return this.id;
    }

    public @NotNull String getName() {

        return this.name;
    }

    public void setName(@NotNull String name) {

        this.name = name;
    }

    public String getSynopsis() {

        return this.synopsis;
    }

    public void setSynopsis(String synopsis) {

        this.synopsis = synopsis;
    }

    public String getGenres() {

        return this.genres;
    }

    public void setGenres(String genres) {

        this.genres = genres;
    }

    public String getThemes() {

        return this.themes;
    }

    public void setThemes(String themes) {

        this.themes = themes;
    }

    public @NotNull AnimeStatus getStatus() {

        return this.status;
    }

    public void setStatus(@NotNull AnimeStatus status) {

        this.status = status;
    }

    public Set<Interest> getInterests() {

        return this.interests;
    }

    public @NotNull DiscordUser getAddedBy() {

        return this.addedBy;
    }

    public void setAddedBy(DiscordUser addedBy) {

        this.addedBy = addedBy;
    }

    public String getImage() {

        return this.image;
    }

    public void setImage(String image) {

        this.image = image;
    }

    public @Nullable String getLink() {

        return this.link;
    }

    public void setLink(@Nullable String link) {

        this.link = link;
    }

    public long getWatched() {

        return this.watched;
    }

    public void setWatched(long watched) {

        this.watched = watched;
    }

    public long getTotal() {

        return this.total;
    }

    public void setTotal(long total) {

        this.total = total;
    }

    public long getEpisodeDuration() {

        return this.episodeDuration;
    }

    public void setEpisodeDuration(long episodeDuration) {

        this.episodeDuration = episodeDuration;
    }

    public @Nullable Long getAnnounceMessage() {

        return this.announceMessage;
    }

    public void setAnnounceMessage(@Nullable Long announceMessage) {

        this.announceMessage = announceMessage;
    }

    public @NotNull ZonedDateTime getAddedAt() {

        return this.addedAt;
    }

    public void setAddedAt(@NotNull ZonedDateTime addedAt) {

        this.addedAt = addedAt;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Anime anime = (Anime) o;
        return Objects.equals(this.getId(), anime.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId());
    }

    @Override
    public int compareTo(@NotNull Anime o) {

        int comp = this.getName().compareTo(o.getName());

        if (comp == 0) {
            return this.getId().compareTo(o.getId());
        }

        return comp;
    }

    public Command.Choice asChoice() {
        if (this.getName().length() > 100) {
            return new Command.Choice(String.format("%s...", this.getName().substring(0, 90)), this.getId());
        }
        return new Command.Choice(this.getName(), this.getId());
    }

}
