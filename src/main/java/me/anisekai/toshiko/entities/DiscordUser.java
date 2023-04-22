package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
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

    public DiscordUser() {}

    public DiscordUser(@NotNull User user) {

        this.updateWith(user);
        this.active = false;
        this.admin  = false;
    }

    public void updateWith(@NotNull User user) {

        this.id            = user.getIdLong();
        this.username      = user.getName();
        this.discriminator = user.getDiscriminator();
    }

    public @NotNull Long getId() {

        return this.id;
    }

    public @NotNull String getUsername() {

        return this.username;
    }

    public @NotNull String getDiscriminator() {

        return this.discriminator;
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
