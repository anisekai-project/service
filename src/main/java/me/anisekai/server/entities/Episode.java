package me.anisekai.server.entities;

import jakarta.persistence.*;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.server.interfaces.IEpisode;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Episode implements IEpisode<Anime> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Anime anime;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    public Long getId() {

        return this.id;
    }

    @Override
    public Anime getAnime() {

        return this.anime;
    }

    @Override
    public void setAnime(Anime anime) {

        this.anime = anime;
    }

    @Override
    public int getNumber() {

        return this.number;
    }

    @Override
    public void setNumber(int number) {

        this.number = number;
    }

    public ZonedDateTime getCreatedAt() {

        return this.createdAt;
    }

    public ZonedDateTime getUpdatedAt() {

        return this.updatedAt;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof IEpisode<?> episode) return EntityUtils.equals(this, episode);
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
