package me.anisekai.server.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.server.interfaces.IDiscordUser;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity(name = "user")
public class DiscordUser implements IDiscordUser {

    @Id
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column
    private @Nullable String emote;

    @Column(nullable = false)
    private boolean active = false;

    @Column(nullable = false)
    private boolean administrator = false;

    @Column(nullable = false)
    private boolean websiteAccess = false;

    @Column
    private String key;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

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

    @Nullable
    @Override
    public String getEmote() {

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
    public boolean isAdministrator() {

        return this.administrator;
    }

    @Override
    public void setAdministrator(boolean administrator) {

        this.administrator = administrator;
    }

    @Override
    public boolean hasWebsiteAccess() {

        return this.websiteAccess;
    }

    @Override
    public void setWebsiteAccess(boolean websiteAccess) {

        this.websiteAccess = websiteAccess;
    }

    @Override
    public String getKey() {

        return this.key;
    }

    @Override
    public void setKey(String key) {

        this.key = key;
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

        if (o instanceof IDiscordUser user) return EntityUtils.equals(this, user);
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
