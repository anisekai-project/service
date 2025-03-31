package me.anisekai.server.entities;

import jakarta.persistence.*;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.server.interfaces.IVoter;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class Voter implements IVoter<DiscordUser, Selection, Anime> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Selection selection;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private DiscordUser user;

    @Column(nullable = false)
    private long amount;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Anime> votes;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public Long getId() {

        return this.id;
    }

    @Override
    public Selection getSelection() {

        return this.selection;
    }

    @Override
    public void setSelection(Selection selection) {

        this.selection = selection;
    }

    @Override
    public DiscordUser getUser() {

        return this.user;
    }

    @Override
    public void setUser(DiscordUser user) {

        this.user = user;
    }

    @Override
    public long getAmount() {

        return this.amount;
    }

    @Override
    public void setAmount(long amount) {

        this.amount = amount;
    }

    @Override
    public Set<Anime> getVotes() {

        return this.votes;
    }

    @Override
    public void setVotes(Set<Anime> votes) {

        this.votes = votes;
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

        if (o instanceof IVoter<?, ?, ?> voter) return EntityUtils.equals(this, voter);
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
