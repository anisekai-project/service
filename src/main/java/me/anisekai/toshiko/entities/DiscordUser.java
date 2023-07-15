package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.Nullable;

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
public class DiscordUser {

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

    public Long getId() {

        return this.id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getUsername() {

        return this.username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getDiscriminator() {

        return this.discriminator;
    }

    public void setDiscriminator(String discriminator) {

        this.discriminator = discriminator;
    }

    public @Nullable String getEmote() {

        return this.emote;
    }

    public void setEmote(@Nullable String emote) {

        this.emote = emote;
    }

    public boolean isActive() {

        return this.active;
    }

    public void setActive(boolean active) {

        this.active = active;
    }

    public boolean isAdmin() {

        return this.admin;
    }

    public void setAdmin(boolean admin) {

        this.admin = admin;
    }

    public boolean isWebAccess() {

        return this.webAccess;
    }

    public void setWebAccess(boolean webAccess) {

        this.webAccess = webAccess;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DiscordUser that = (DiscordUser) o;
        return this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId());
    }

}
