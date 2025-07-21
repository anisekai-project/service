package fr.anisekai.web;

import fr.anisekai.ApplicationConfiguration;
import fr.anisekai.sanctum.interfaces.isolation.IsolationSession;
import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.services.UserService;
import fr.anisekai.web.data.AuthenticationKey;
import fr.anisekai.web.data.Session;
import fr.anisekai.web.data.SessionToken;
import fr.anisekai.web.enums.SessionType;
import fr.anisekai.web.packets.AuthTokenPacket;
import fr.anisekai.web.packets.RefreshTokenPacket;
import fr.anisekai.web.packets.UserInfoPacket;
import fr.anisekai.web.packets.results.DiscordIdentity;
import fr.anisekai.web.packets.results.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationManager.class);

    private static final long INACTIVITY_TIMEOUT  = Duration.of(1, ChronoUnit.HOURS).getSeconds();
    private static final long TOKEN_REFRESH_LIMIT = Duration.of(10, ChronoUnit.MINUTES).getSeconds();

    private final ApplicationConfiguration.Discord.OAuth configuration;
    private final UserService                            userService;

    private final Map<SessionToken, Session>          sessions   = new HashMap<>();
    private final Map<SessionToken, IsolationSession> isolations = new HashMap<>();

    public AuthenticationManager(ApplicationConfiguration configuration, UserService userService) {

        this.configuration = configuration.getDiscord().getOauth();
        this.userService   = userService;
    }

    @Scheduled(cron = "0 */5 * * * *")
    private void checkTokens() {

        List<Session> sessions = this.sessions
                .values()
                .stream()
                .filter(Session::isOAuthSession)
                .toList();

        for (Session session : sessions) {
            UserToken userToken = session.getOauthToken();

            if (userToken.isInactive(INACTIVITY_TIMEOUT)) {
                this.sessions.remove(session.getToken());
                // TODO: Invalidate token
                continue;
            }

            if (userToken.willExpire(TOKEN_REFRESH_LIMIT)) {
                try {
                    new RefreshTokenPacket(userToken).complete();
                } catch (Exception e) {
                    this.sessions.remove(session.getToken());
                }
            }
        }
    }

    public Optional<Session> resolveSession(SessionToken token) {

        return Optional.ofNullable(this.sessions.get(token));
    }

    public Optional<IsolationSession> resolveIsolation(SessionToken token) {

        return Optional.ofNullable(this.isolations.get(token));
    }

    public Session authenticate(AuthenticationKey auth) throws Exception {

        if (auth.type() == SessionType.APP) {
            throw new IllegalArgumentException("Cannot authenticate with an application key.");
        }

        LOGGER.info("Authentication request using {}", auth);
        AuthTokenPacket authTokenPacket = new AuthTokenPacket(this.configuration, auth.key());
        UserToken       userToken       = authTokenPacket.complete();

        LOGGER.debug("Authentication successful: {}", userToken);
        LOGGER.debug("Requesting user identity...");
        UserInfoPacket  userInfoPacket = new UserInfoPacket(userToken);
        DiscordIdentity identity       = userInfoPacket.complete();

        LOGGER.info("Welcome ! ({})", identity);

        DiscordUser user    = this.userService.ensureUserExists(identity);
        Session     session = new Session(auth, user, userToken);
        this.sessions.put(session.getToken(), session);
        return session;
    }

    public Session exchange(AuthenticationKey auth) {

        if (auth.type() == SessionType.OAUTH) {
            throw new IllegalArgumentException("Cannot exchange key with a oauth code.");
        }

        Optional<DiscordUser> optUser = this.userService.getByApiKey(auth.key());

        if (optUser.isEmpty()) {
            throw new IllegalArgumentException("Invalid application key");
        }

        DiscordUser user    = optUser.get();
        Session     session = new Session(auth, user);
        this.sessions.put(session.getToken(), session);
        return session;
    }

}
