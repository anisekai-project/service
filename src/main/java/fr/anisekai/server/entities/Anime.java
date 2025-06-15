package fr.anisekai.server.entities;


import fr.anisekai.wireless.remote.enums.AnimeList;
import fr.anisekai.wireless.remote.interfaces.AnimeEntity;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.*;
import fr.anisekai.server.entities.adapters.AnimeEventAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Entity
public class Anime implements AnimeEventAdapter, Comparable<Anime> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String group;

    @Column(nullable = false)
    private byte order = 1;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnimeList list;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column
    private String tags;

    @Column
    private String thumbnailUrl;

    @Column(nullable = false, unique = true)
    private String url;

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
    private Long announcementId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "anime")
    private Set<Episode> episodes;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public Long getId() {

        return this.id;
    }

    @Override
    public @NotNull String getGroup() {

        return this.group;
    }

    @Override
    public void setGroup(@NotNull String group) {

        this.group = group;
    }

    @Override
    public byte getOrder() {

        return this.order;
    }

    @Override
    public void setOrder(byte order) {

        this.order = order;
    }

    @Override
    public @NotNull String getTitle() {

        return this.title;
    }

    @Override
    public void setTitle(@NotNull String title) {

        this.title = title;
    }

    @Override
    public @NotNull AnimeList getList() {

        return this.list;
    }

    @Override
    public void setList(@NotNull AnimeList list) {

        this.list = list;
    }

    @Override
    public @Nullable String getSynopsis() {

        return this.synopsis;
    }

    @Override
    public void setSynopsis(String synopsis) {

        this.synopsis = synopsis;
    }

    @Override
    public @NotNull List<String> getTags() {

        return Arrays.asList(this.tags.split(","));
    }

    @Override
    public void setTags(@NotNull List<String> tags) {

        this.tags = String.join(",", tags);
    }

    @Override
    public @Nullable String getThumbnailUrl() {

        return this.thumbnailUrl;
    }

    @Override
    public void setThumbnailUrl(String thumbnailUrl) {

        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public @NotNull String getUrl() {

        return this.url;
    }

    @Override
    public void setUrl(String url) {

        this.url = url;
    }

    @Override
    public @Nullable Pattern getTitleRegex() {

        return this.titleRegex == null ? null : Pattern.compile(this.titleRegex);
    }

    @Override
    public void setTitleRegex(@Nullable Pattern titleRegex) {

        this.titleRegex = titleRegex == null ? null : titleRegex.pattern();
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
    public @NotNull DiscordUser getAddedBy() {

        return this.addedBy;
    }

    @Override
    public void setAddedBy(@NotNull DiscordUser addedBy) {

        this.addedBy = addedBy;
    }

    @Override
    public @Nullable Long getAnilistId() {

        return this.anilistId;
    }

    @Override
    public void setAnilistId(Long anilistId) {

        this.anilistId = anilistId;
    }

    @Override
    public Long getAnnouncementId() {

        return this.announcementId;
    }

    @Override
    public void setAnnouncementId(Long announcementId) {

        this.announcementId = announcementId;
    }

    public Set<Episode> getEpisodes() {

        return this.episodes;
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

        if (o instanceof AnimeEntity<?> anime) return EntityUtils.equals(this, anime);
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

    @Override
    public int compareTo(@NotNull Anime o) {

        return EntityUtils.compare(
                this,
                o,
                Comparator.comparing(Anime::getList),
                Comparator.comparing(Anime::getTitle)
        );
    }

}
