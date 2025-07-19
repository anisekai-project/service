package fr.anisekai.web.interceptors;

import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.web.AuthenticationManager;
import fr.anisekai.web.annotations.RequireAuth;
import fr.anisekai.web.data.Session;
import fr.anisekai.web.data.SessionToken;
import fr.anisekai.web.enums.SessionType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    /**
     * Represents the authentication and authorization policy of a route based on the {@link RequireAuth} annotation.
     */
    private static class RouteRule {

        private final boolean              authRequired;
        private       boolean              adminRequired       = false;
        private       boolean              guestsAllowed       = true;
        private       EnumSet<SessionType> sessionTypesAllowed = EnumSet.of(SessionType.OAUTH, SessionType.APP);

        public RouteRule(@Nullable RequireAuth auth) {

            this.authRequired = auth != null;

            if (this.authRequired) {
                this.adminRequired       = auth.requireAdmin();
                this.guestsAllowed       = auth.allowGuests();
                this.sessionTypesAllowed = EnumSet.noneOf(SessionType.class);
                Collections.addAll(this.sessionTypesAllowed, auth.allowedSessionTypes());
            }
        }

        public boolean isAuthRequired() {

            return this.authRequired;
        }

        public boolean canAccess(Session session) {

            DiscordUser  user  = session.getIdentity();
            SessionToken token = session.getToken();

            boolean guestCheckPass = !user.isGuest() || this.guestsAllowed;
            boolean adminCheckPass = user.isAdministrator() || !this.adminRequired;
            boolean typeCheckPass  = this.sessionTypesAllowed.contains(token.auth().type());

            return guestCheckPass && adminCheckPass && typeCheckPass;
        }

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    private final AuthenticationManager manager;

    public AuthenticationInterceptor(AuthenticationManager manager) {

        this.manager = manager;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod method)) {
            return true; // Not a controller method — allow
        }

        String    route = String.format("%s %s", request.getMethod(), request.getRequestURI());
        RouteRule rule  = new RouteRule(method.getMethodAnnotation(RequireAuth.class));

        if (!rule.isAuthRequired()) {
            LOGGER.trace("[{}] Allowed anonymous access.", route);
            return true; // No @RequireAuth — allow
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            LOGGER.info("[{}] Denied access: No bearer provided.", route);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header missing or invalid");
            return false;
        }

        String       bearer = authHeader.substring(7).trim();
        SessionToken token;

        try {
            token = SessionToken.fromBase64(bearer);
        } catch (Exception e) {
            LOGGER.warn("[{}] Denied access: Bearer format invalid.", route, e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token format");
            return false;
        }

        Session session = this.manager.resolveSession(token).orElse(null);
        if (session == null) {
            LOGGER.warn("[{}] Denied access: Session not found.", route);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session not found");
            return false;
        }

        if (!rule.canAccess(session)) {
            LOGGER.info("[{}] ({}) Denied access: Rules mismatch.", route, session.getIdentity().getId());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return false;
        }

        // Optionally store session for later use
        request.setAttribute("session", session);
        LOGGER.trace("[{}] ({}) Accessed secured resource.", route, session.getIdentity().getId());
        return true; // Allow
    }

}
