package fr.anisekai.server.entities.adapters;

import fr.anisekai.wireless.api.persistence.interfaces.Entity;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface DiscordTokenEventAdapter<S extends SessionEventAdapter<?>> extends Entity<UUID> {

    S getSession();

    void setSession(S session);

    String getAccessToken();

    void setAccessToken(String accessToken);

    String getTokenType();

    void setTokenType(String tokenType);

    ZonedDateTime getExpiresAt();

    void setExpiresAt(ZonedDateTime expiresAt);

    String getRefreshToken();

    void setRefreshToken(String refreshToken);

    String getScope();

    void setScope(String scope);

}
