package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import me.anisekai.toshiko.interfaces.entities.IUser;
import me.anisekai.toshiko.utils.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Objects;

@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_USER_DISCORD",
                        columnNames = {"username", "discriminator"}
                )
        }
)
@Entity
public class DiscordUser implements IUser, Comparable<DiscordUser> {

    // <editor-fold desc="Entity Structure">

    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String discriminator;

    @Column
    private @Nullable String emote;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean admin;

    @Column(nullable = false)
    private boolean webAccess;

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
    public String getUsername() {

        return this.username;
    }

    @Override
    public void setUsername(String username) {

        this.username = username;
    }

    @Override
    public String getDiscriminator() {

        return this.discriminator;
    }

    @Override
    public void setDiscriminator(String discriminator) {

        this.discriminator = discriminator;
    }

    @Override
    public @Nullable String getEmote() {

        return this.emote;
    }

    @Override
    public void setEmote(@Nullable String emote) {

        this.emote = emote;
    }

    @Override
    public boolean isActive() {

        return this.active;
    }

    @Override
    public void setActive(boolean active) {

        this.active = active;
    }

    @Override
    public boolean isAdmin() {

        return this.admin;
    }

    @Override
    public void setAdmin(boolean admin) {

        this.admin = admin;
    }

    @Override
    public boolean hasWebAccess() {

        return false;
    }

    /**
     * @deprecated Use {@link IUser#hasWebAccess()} instead.
     */
    @Deprecated
    public boolean isWebAccess() {

        return this.webAccess;
    }

    @Override
    public void setWebAccess(boolean webAccess) {

        this.webAccess = webAccess;
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
    @Transient
    public boolean isNew() {

        return this.getId() == null;
    }

    @Override
    public boolean equals(Object o) {

        return o instanceof IUser other && EntityUtils.equals(this, other);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId());
    }

    @Override
    public int compareTo(@NotNull DiscordUser other) {

        return EntityUtils.compare(
                this,
                other,
                Comparator.comparing(DiscordUser::getUsername)
        );
    }

    @PostLoad
    public void onLoad() {

        this.createdAt     = this.createdAt.withZoneSameInstant(ZoneId.systemDefault());
        this.updatedAt     = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

}
