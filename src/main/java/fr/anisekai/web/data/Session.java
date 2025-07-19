package fr.anisekai.web.data;

import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.web.enums.SessionType;
import fr.anisekai.web.packets.results.UserToken;

import java.time.Instant;
import java.util.UUID;

public class Session {

    private final SessionToken token;
    private final UserToken    oauthToken;
    private final DiscordUser  identity;
    private final Instant      createdAt;

    public Session(AuthenticationKey key, DiscordUser user, UserToken oauthToken) {

        this.token      = new SessionToken(key, UUID.randomUUID());
        this.oauthToken = oauthToken;
        this.identity   = user;
        this.createdAt  = Instant.now();
    }

    public Session(AuthenticationKey key, DiscordUser user) {

        this.token      = new SessionToken(key, UUID.randomUUID());
        this.oauthToken = null;
        this.identity   = user;
        this.createdAt  = Instant.now();
    }

    public SessionToken getToken() {

        return this.token;
    }

    public UserToken getOauthToken() {

        return this.oauthToken;
    }

    public DiscordUser getIdentity() {

        return this.identity;
    }

    public Instant getCreatedAt() {

        return this.createdAt;
    }

    public boolean isOAuthSession() {

        return this.token.auth().type() == SessionType.OAUTH;
    }

    public boolean isApplicationSession() {

        return this.token.auth().type() == SessionType.APP;
    }

}
