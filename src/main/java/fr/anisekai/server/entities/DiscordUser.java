package fr.anisekai.server.entities;

import fr.anisekai.server.entities.adapters.UserEventAdapter;
import fr.anisekai.wireless.remote.interfaces.UserEntity;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity(name = "user")
public class DiscordUser implements UserEventAdapter {

    @Id
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String avatarUrl;

    @Column
    private String emote;

    @Column(nullable = false)
    private boolean active = false;

    @Column(nullable = false)
    private boolean administrator = false;

    @Column(nullable = false)
    private boolean guest = true;

    @Column
    private String apiKey;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

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
    public @NotNull String getUsername() {

        return this.username;
    }

    @Override
    public void setUsername(@NotNull String username) {

        this.username = username;
    }

    @Override
    public @Nullable String getNickname() {

        return this.nickname;
    }

    @Override
    public void setNickname(String nickname) {

        this.nickname = nickname;
    }

    @Override
    public @NotNull String getAvatarUrl() {

        return this.avatarUrl;
    }

    @Override
    public void setAvatarUrl(@NotNull String avatarUrl) {

        this.avatarUrl = avatarUrl;
    }

    @Override
    public @Nullable String getEmote() {

        return this.emote;
    }

    @Override
    public void setEmote(String emote) {

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
    public boolean isGuest() {

        return this.guest;
    }

    @Override
    public void setGuest(boolean guest) {

        this.guest = guest;
    }

    @Override
    public @Nullable String getApiKey() {

        return this.apiKey;
    }

    @Override
    public void setApiKey(String apiKey) {

        this.apiKey = apiKey;
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

        if (o instanceof UserEntity user) return EntityUtils.equals(this, user);
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
