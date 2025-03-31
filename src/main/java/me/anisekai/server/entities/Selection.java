package me.anisekai.server.entities;

import jakarta.persistence.*;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.server.enums.Season;
import me.anisekai.server.enums.SelectionStatus;
import me.anisekai.server.interfaces.ISelection;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class Selection implements ISelection<Anime> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Season season;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SelectionStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Anime> animes;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public Long getId() {

        return this.id;
    }

    public Season getSeason() {

        return this.season;
    }

    public void setSeason(Season season) {

        this.season = season;
    }

    public int getYear() {

        return this.year;
    }

    public void setYear(int year) {

        this.year = year;
    }

    @Override
    public SelectionStatus getStatus() {

        return this.status;
    }

    @Override
    public void setStatus(SelectionStatus status) {

        this.status = status;
    }

    public Set<Anime> getAnimes() {

        return this.animes;
    }

    public void setAnimes(Set<Anime> animes) {

        this.animes = animes;
    }

    public ZonedDateTime getCreatedAt() {

        return this.createdAt;
    }

    public ZonedDateTime getUpdatedAt() {

        return this.updatedAt;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof ISelection<?> selection) return EntityUtils.equals(this, selection);
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
