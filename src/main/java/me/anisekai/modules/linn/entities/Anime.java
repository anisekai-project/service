package me.anisekai.modules.linn.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import me.anisekai.api.plannifier.interfaces.WatchTarget;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.linn.interfaces.IAnime;
import me.anisekai.modules.shizue.entities.Interest;
import me.anisekai.api.persistence.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Anime implements IAnime, WatchTarget, Comparable<Anime> {

    // <editor-fold desc="Entity Structure">

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 32_768, columnDefinition = "LONGTEXT")
    private String synopsis;

    @Column
    private String genres;

    @Column
    private String themes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnimeStatus status;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
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

    @Column
    private String rssMatch;

    @Column
    private String diskPath;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    // </editor-fold>

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "anime_id")
    @JsonIgnore
    private Set<Interest> interests = new HashSet<>();

    public Anime() {}

    // <editor-fold desc="Getters / Setters">

    @Override
    public Long getId() {

        return this.id;
    }

    @Override
    public void setId(Long id) {

        this.id = id;
    }

    @Override
    public String getName() {

        return this.name;
    }

    @Override
    public void setName(String name) {

        this.name = name;
    }

    @Override
    public String getSynopsis() {

        return this.synopsis;
    }

    @Override
    public void setSynopsis(String synopsis) {

        this.synopsis = synopsis;
    }

    @Override
    public String getGenres() {

        return this.genres;
    }

    @Override
    public void setGenres(String genres) {

        this.genres = genres;
    }

    @Override
    public String getThemes() {

        return this.themes;
    }

    @Override
    public void setThemes(String themes) {

        this.themes = themes;
    }

    @Override
    public AnimeStatus getStatus() {

        return this.status;
    }

    @Override
    public void setStatus(AnimeStatus status) {

        this.status = status;
    }

    @Override
    public Set<Interest> getInterests() {

        return this.interests;
    }

    @Override
    public void setInterests(Set<Interest> interests) {

        this.interests = interests;
    }

    @Override
    public DiscordUser getAddedBy() {

        return this.addedBy;
    }

    @Override
    public void setAddedBy(DiscordUser addedBy) {

        this.addedBy = addedBy;
    }

    @Override
    public String getLink() {

        return this.link;
    }

    @Override
    public void setLink(String link) {

        this.link = link;
    }

    @Override
    public String getImage() {

        return this.image;
    }

    @Override
    public void setImage(String image) {

        this.image = image;
    }

    @Override
    public long getWatched() {

        return this.watched;
    }

    @Override
    public void setWatched(long watched) {

        this.watched = watched;
    }

    @Override
    public long getTotal() {

        return this.total;
    }

    @Override
    public void setTotal(long total) {

        this.total = total;
    }

    /**
     * Retrieve the number of episode watched.
     *
     * @return Number of episode watched.
     */
    @Override
    public long getEpisodeWatched() {

        return this.watched;
    }

    /**
     * Define the number of episode watched.
     *
     * @param episodeWatched
     *         Number of episode watched.
     */
    @Override
    public void setEpisodeWatched(long episodeWatched) {

        this.watched = episodeWatched;
    }

    /**
     * Retrieve the number of episode in total.
     *
     * @return Number of episode in total
     */
    @Override
    public long getEpisodeCount() {

        return this.total;
    }

    /**
     * Define the number of episode in total.
     *
     * @param episodeCount
     *         Number of episode in total
     */
    @Override
    public void setEpisodeCount(long episodeCount) {

        this.total = episodeCount;
    }

    @Override
    public long getEpisodeDuration() {

        return this.episodeDuration;
    }

    @Override
    public void setEpisodeDuration(long episodeDuration) {

        this.episodeDuration = episodeDuration;
    }

    @Override
    public Long getAnnounceMessage() {

        return this.announceMessage;
    }

    @Override
    public void setAnnounceMessage(Long announceMessage) {

        this.announceMessage = announceMessage;
    }

    @Override
    public String getRssMatch() {

        return this.rssMatch;
    }

    @Override
    public void setRssMatch(String rssMatch) {

        this.rssMatch = rssMatch;
    }

    @Override
    public String getDiskPath() {

        return this.diskPath;
    }

    @Override
    public void setDiskPath(String diskPath) {

        this.diskPath = diskPath;
    }

    @Override
    public ZonedDateTime getCreatedAt() {

        return this.createdAt;
    }

    @Override
    public void setCreatedAt(ZonedDateTime createdAt) {

        this.createdAt = createdAt;
    }

    @Override
    public ZonedDateTime getUpdatedAt() {

        return this.updatedAt;
    }

    @Override
    public void setUpdatedAt(ZonedDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }

    // </editor-fold>

    @Override
    @Transient
    public boolean isNew() {

        return this.getId() == null;
    }

    @Override
    public boolean equals(Object o) {

        return o instanceof IAnime anime && EntityUtils.equals(this, anime, IAnime::getLink);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId());
    }

    @Override
    public int compareTo(@NotNull Anime other) {

        return EntityUtils.compare(
                this,
                other,
                Comparator.comparing(Anime::getName),
                Comparator.comparing(Anime::getId)
        );
    }

    @PrePersist
    public void prePersist() {

        this.createdAt = this.createdAt.withZoneSameInstant(ZoneId.systemDefault());
        this.updatedAt = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

    @PreUpdate
    public void preUpdate() {

        this.updatedAt = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

}
