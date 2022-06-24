package me.anisekai.toshiko.entities;

import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.util.Objects;

@Table(name = "user",
       uniqueConstraints = {
               @UniqueConstraint(name = "UK_USER_DISCORD",
                                 columnNames = {"username", "discriminator"})
       })
@Entity
public class DiscordUser {

    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String discriminator;

    @Column(unique = true)
    private @Nullable String emote;

    public DiscordUser() {}

    public DiscordUser(User user) {

        this.updateWith(user);
    }

    public void updateWith(User user) {

        this.id            = user.getIdLong();
        this.username      = user.getName();
        this.discriminator = user.getDiscriminator();
    }

    public Long getId() {

        return this.id;
    }

    public String getUsername() {

        return this.username;
    }

    public String getDiscriminator() {

        return this.discriminator;
    }

    public @Nullable String getEmote() {

        return this.emote;
    }

    public void setEmote(@Nullable String emote) {

        this.emote = emote;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        DiscordUser that = (DiscordUser) o;
        return this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId());
    }

}
