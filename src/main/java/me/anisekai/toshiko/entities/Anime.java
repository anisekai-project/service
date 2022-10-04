package me.anisekai.toshiko.entities;

import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.interfaces.AnimeProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Anime implements Comparable<Anime> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false,
            unique = true)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnimeStatus status;

    @ManyToOne(optional = false)
    private DiscordUser addedBy;

    @Column(nullable = false,
            unique = true)
    private String link;

    @Column(nullable = false)
    private long watched = 0;

    @Column(nullable = false)
    private long total = 0;

    @Column
    private Long announceMessage;

    @Column(nullable = false)
    private LocalDateTime addedAt;

    public Anime() {}

    public Anime(DiscordUser user, AnimeProvider provider) {

        this(user, provider, provider.getPublicationState().getStatus());
    }

    public Anime(DiscordUser user, AnimeProvider provider, AnimeStatus status) {

        this.name    = provider.getName();
        this.status  = status;
        this.addedBy = user;
        this.link    = provider.getUrl();
        this.addedAt = LocalDateTime.now().withNano(0);
        this.total   = provider.getEpisodeCount().orElse(-1L);
    }

    public Long getId() {

        return this.id;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public AnimeStatus getStatus() {

        return this.status;
    }

    public void setStatus(AnimeStatus status) {

        this.status = status;
    }

    public DiscordUser getAddedBy() {

        return this.addedBy;
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

    public Long getAnnounceMessage() {

        return this.announceMessage;
    }

    public void setAnnounceMessage(Long announceMessage) {

        this.announceMessage = announceMessage;
    }

    public LocalDateTime getAddedAt() {

        return this.addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {

        this.addedAt = addedAt;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
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

}
