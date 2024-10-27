package me.anisekai.modules.shizue.entities;

import jakarta.persistence.*;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.enums.SeasonalSelectionState;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalSelection;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class SeasonalSelection implements ISeasonalSelection {

    // <editor-fold desc="Entity Structure">

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Anime> animes;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "seasonalSelection")
    private Set<SeasonalVoter> voters;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "seasonalSelection")
    private Set<SeasonalVote> votes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeasonalSelectionState state = SeasonalSelectionState.OPENED;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    // </editor-fold>

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
    public Set<Anime> getAnimes() {

        return this.animes;
    }

    @Override
    public void setAnimes(Set<Anime> animes) {

        this.animes = animes;
    }

    @Override
    public Set<SeasonalVoter> getVoters() {

        return this.voters;
    }

    @Override
    public void setVoters(Set<SeasonalVoter> voters) {

        this.voters = voters;
    }

    @Override
    public Set<SeasonalVote> getVotes() {

        return this.votes;
    }

    @Override
    public void setVotes(Set<SeasonalVote> votes) {

        this.votes = votes;
    }

    @Override
    public SeasonalSelectionState getState() {

        return this.state;
    }

    @Override
    public void setState(SeasonalSelectionState state) {

        this.state = state;
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

        return this.id == null;
    }

    @Override
    public boolean equals(Object o) {

        return o instanceof ISeasonalSelection other && EntityUtils.equals(this, other);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId());
    }

    @PostLoad
    public void onLoad() {

        this.createdAt = this.createdAt.withZoneSameInstant(ZoneId.systemDefault());
        this.updatedAt = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

}
