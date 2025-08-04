package fr.anisekai.server.entities;

import fr.anisekai.server.entities.adapters.SelectionEventAdapter;
import fr.anisekai.wireless.remote.enums.AnimeSeason;
import fr.anisekai.wireless.remote.enums.SelectionStatus;
import fr.anisekai.wireless.remote.interfaces.SelectionEntity;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class Selection implements SelectionEventAdapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnimeSeason season;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SelectionStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Anime> animes;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public Long getId() {

        return this.id;
    }

    @Override
    public @NotNull AnimeSeason getSeason() {

        return this.season;
    }

    @Override
    public void setSeason(@NotNull AnimeSeason season) {

        this.season = season;
    }

    @Override
    public int getYear() {

        return this.year;
    }

    @Override
    public void setYear(int year) {

        this.year = year;
    }

    @Override
    public @NotNull SelectionStatus getStatus() {

        return this.status;
    }

    @Override
    public void setStatus(@NotNull SelectionStatus status) {

        this.status = status;
    }

    @Override
    public @NotNull Set<Anime> getAnimes() {

        return this.animes;
    }

    @Override
    public void setAnimes(@NotNull Set<Anime> animes) {

        this.animes = animes;
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

        if (o instanceof SelectionEntity<?> selection) return EntityUtils.equals(this, selection);
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
