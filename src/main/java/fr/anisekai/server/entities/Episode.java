package fr.anisekai.server.entities;

import fr.anisekai.wireless.remote.interfaces.EpisodeEntity;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.*;
import fr.anisekai.server.entities.adapters.EpisodeEventAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class Episode implements EpisodeEventAdapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Anime anime;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private boolean ready = false;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "episode")
    private Set<Track> tracks;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public Long getId() {

        return this.id;
    }

    @Override
    public @NotNull Anime getAnime() {

        return this.anime;
    }

    @Override
    public void setAnime(@NotNull Anime anime) {

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

    @Override
    public boolean isReady() {

        return this.ready;
    }

    @Override
    public void setReady(boolean ready) {

        this.ready = ready;
    }

    public Set<Track> getTracks() {

        return this.tracks;
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

        if (o instanceof EpisodeEntity<?> episode) return EntityUtils.equals(this, episode);
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
