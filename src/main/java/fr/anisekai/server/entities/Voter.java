package fr.anisekai.server.entities;

import fr.anisekai.wireless.remote.interfaces.VoterEntity;
import fr.anisekai.wireless.remote.keys.VoterKey;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.*;
import fr.anisekai.server.entities.adapters.VoterEventAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@IdClass(VoterKey.class)
public class Voter implements VoterEventAdapter {

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Selection selection;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private DiscordUser user;

    @Column(nullable = false)
    private short amount;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Anime> votes;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public @NotNull Selection getSelection() {

        return this.selection;
    }

    @Override
    public void setSelection(@NotNull Selection selection) {

        this.selection = selection;
    }

    @Override
    public @NotNull DiscordUser getUser() {

        return this.user;
    }

    @Override
    public void setUser(@NotNull DiscordUser user) {

        this.user = user;
    }

    @Override
    public short getAmount() {

        return this.amount;
    }

    @Override
    public void setAmount(short amount) {

        this.amount = amount;
    }

    @Override
    public @NotNull Set<Anime> getVotes() {

        return this.votes;
    }

    @Override
    public void setVotes(@NotNull Set<Anime> votes) {

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

        if (o instanceof VoterEntity<?, ?, ?> voter) return EntityUtils.equals(this, voter);
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
