package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import me.anisekai.toshiko.interfaces.entities.ISeasonalSelection;
import me.anisekai.toshiko.utils.EntityUtils;
import net.dv8tion.jda.api.interactions.commands.Command;

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
    private boolean closed;

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
    public boolean isClosed() {

        return this.closed;
    }

    @Override
    public void setClosed(boolean closed) {

        this.closed = closed;
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

    // TODO: Move this elsewhere.
    @Deprecated
    public Command.Choice asChoice() {

        if (this.getName().length() > 100) {
            return new Command.Choice(String.format("%s...", this.getName().substring(0, 90)), this.getId());
        }
        return new Command.Choice(this.getName(), this.getId());
    }

    @PostLoad
    public void onLoad() {

        this.createdAt     = this.createdAt.withZoneSameInstant(ZoneId.systemDefault());
        this.updatedAt     = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

}
