package me.anisekai.modules.shizue.entities;

import jakarta.persistence.*;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.shizue.interfaces.entities.IWatchlist;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

@Entity
public class Watchlist implements IWatchlist {

    // <editor-fold desc="Entity Structure">

    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnimeStatus status;

    @Column
    private Long messageId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "status")
    private Set<Anime> animes;

    @Column(nullable = false)
    private int order;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    // </editor-fold>

    /**
     * As {@link IWatchlist} primary key is always defined even when not persisted, this field is used to keep track of
     * this entity instance state.
     */
    @Transient
    private transient boolean created = true;

    public Watchlist() {}

    public Watchlist(AnimeStatus status) {

        this.status = status;
        this.order  = status.ordinal();
    }

    // <editor-fold desc="Getters / Setters">

    @Override
    public AnimeStatus getId() {

        return this.status;
    }

    @Override
    public void setId(AnimeStatus id) {

        throw new UnsupportedOperationException("Cannot set this id. Remove the entity and insert it again if needed.");
    }

    @Override
    public Long getMessageId() {

        return this.messageId;
    }

    @Override
    public void setMessageId(Long messageId) {

        this.messageId = messageId;
    }

    @Override
    public Set<Anime> getAnimes() {

        return this.animes;
    }

    @Override
    public void setAnimes(Set<Anime> animes) {

        this.animes = animes;
    }

    @Override
    public int getOrder() {

        return this.order;
    }

    @Override
    public void setOrder(int order) {

        this.order = order;
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
    public boolean isNew() {

        return this.created;
    }

    @Override
    public boolean equals(Object o) {

        return o instanceof IWatchlist other && EntityUtils.equals(this, other);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.status);
    }

    @Override
    public int compareTo(@NotNull IWatchlist other) {

        return EntityUtils.compare(this, other, Comparator.comparing(IWatchlist::getOrder));
    }

    @PostLoad
    @PostPersist
    private void persisted() {

        this.created   = false;
        this.createdAt = this.createdAt.withZoneSameInstant(ZoneId.systemDefault());
        this.updatedAt = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

}
