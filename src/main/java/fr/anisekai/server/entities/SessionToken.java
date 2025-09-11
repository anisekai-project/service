package fr.anisekai.server.entities;

import fr.anisekai.server.entities.adapters.SessionEventAdapter;
import fr.anisekai.web.enums.TokenType;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
public class SessionToken implements SessionEventAdapter<DiscordUser> {

    @Id
    @JdbcTypeCode(Types.BINARY)
    private UUID id;

    @ManyToOne(optional = false)
    private DiscordUser owner;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TokenType type;

    @Column(nullable = false)
    private ZonedDateTime expiresAt;

    @Column
    private ZonedDateTime revokedAt;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public UUID getId() {

        return this.id;
    }

    @Override
    public void setId(UUID id) {

        this.id = id;
    }

    @Override
    public DiscordUser getOwner() {

        return this.owner;
    }

    @Override
    public void setOwner(DiscordUser owner) {

        this.owner = owner;
    }

    @Override
    public TokenType getType() {

        return this.type;
    }

    @Override
    public void setType(TokenType type) {

        this.type = type;
    }

    @Override
    public ZonedDateTime getExpiresAt() {

        return this.expiresAt;
    }

    @Override
    public void setExpiresAt(ZonedDateTime expiresAt) {

        this.expiresAt = expiresAt;
    }

    @Override
    public ZonedDateTime getRevokedAt() {

        return this.revokedAt;
    }

    @Override
    public void setRevokedAt(ZonedDateTime revokedAt) {

        this.revokedAt = revokedAt;
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

        if (o instanceof SessionEventAdapter<?> session) return EntityUtils.equals(this, session);
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
