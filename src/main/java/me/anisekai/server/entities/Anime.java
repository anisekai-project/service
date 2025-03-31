package me.anisekai.server.entities;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.server.enums.AnimeStatus;
import me.anisekai.server.interfaces.IAnime;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Anime implements IAnime<DiscordUser> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnimeStatus status;

    @Column
    private String synopsis;

    @Column
    private String tags;

    @Column
    private String thumbnail;

    @Column(nullable = false)
    private String nautiljonUrl;

    @Column
    private String titleRegex;

    @Column
    private long watched = 0;

    @Column
    private long total = 0;

    @Column
    private long episodeDuration = 0;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private DiscordUser addedBy;

    @Column
    private Long anilistId;

    @Column
    private @Nullable Long announcementId;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public Long getId() {

        return this.id;
    }

    @Override
    public String getTitle() {

        return this.title;
    }

    @Override
    public void setTitle(String title) {

        this.title = title;
    }

    @Override
    public @NotNull AnimeStatus getWatchlist() {

        return this.status;
    }

    @Override
    public void setWatchlist(@NotNull AnimeStatus animeStatus) {

        this.status = animeStatus;
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
    public String getTags() {

        return this.tags;
    }

    @Override
    public void setTags(String tags) {

        this.tags = tags;
    }

    @Override
    public String getThumbnail() {

        return this.thumbnail;
    }

    @Override
    public void setThumbnail(String thumbnail) {

        this.thumbnail = thumbnail;
    }

    @Override
    public String getNautiljonUrl() {

        return this.nautiljonUrl;
    }

    @Override
    public void setNautiljonUrl(String nautiljonUrl) {

        this.nautiljonUrl = nautiljonUrl;
    }

    @Override
    public String getTitleRegex() {

        return this.titleRegex;
    }

    @Override
    public void setTitleRegex(String titleRegex) {

        this.titleRegex = titleRegex;
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

    @Override
    public long getEpisodeDuration() {

        return this.episodeDuration;
    }

    @Override
    public void setEpisodeDuration(long episodeDuration) {

        this.episodeDuration = episodeDuration;
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
    public Long getAnilistId() {

        return this.anilistId;
    }

    @Override
    public void setAnilistId(Long anilistId) {

        this.anilistId = anilistId;
    }

    @Override
    @Nullable
    public Long getAnnouncementId() {

        return this.announcementId;
    }

    @Override
    public void setAnnouncementId(@Nullable Long announcementId) {

        this.announcementId = announcementId;
    }

    @Override
    public ZonedDateTime getCreatedAt() {

        return this.createdAt;
    }

    @Override
    public ZonedDateTime getUpdatedAt() {

        return this.updatedAt;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof IAnime<?> anime) return EntityUtils.equals(this, anime);
        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(this.getId());
    }

    @PreUpdate
    public void beforeSave() {

        this.updatedAt = ZonedDateTime.now();
    }

}
