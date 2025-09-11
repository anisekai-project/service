package fr.anisekai.server.entities.adapters;

import fr.anisekai.web.enums.TokenType;
import fr.anisekai.wireless.api.persistence.interfaces.Entity;
import fr.anisekai.wireless.remote.interfaces.UserEntity;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface SessionEventAdapter<O extends UserEntity> extends Entity<UUID> {

    O getOwner();

    void setOwner(O owner);

    TokenType getType();

    void setType(TokenType type);

    ZonedDateTime getExpiresAt();

    void setExpiresAt(ZonedDateTime expiresAt);

    ZonedDateTime getRevokedAt();

    void setRevokedAt(ZonedDateTime revokedAt);

}
