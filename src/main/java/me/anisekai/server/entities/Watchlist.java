package me.anisekai.server.entities;

import jakarta.persistence.*;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.server.enums.AnimeStatus;
import me.anisekai.server.interfaces.IWatchlist;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Watchlist implements IWatchlist {

    @Id
    @Enumerated(EnumType.STRING)
    private AnimeStatus id;

    @Column
    private Long messageId;

    @Column(nullable = false)
    private int order;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public AnimeStatus getId() {

        return this.id;
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
    public ZonedDateTime getUpdatedAt() {

        return this.updatedAt;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof IWatchlist watchlist) return EntityUtils.equals(this, watchlist);
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
