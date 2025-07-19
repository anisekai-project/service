package fr.anisekai.web.interceptors;

import fr.anisekai.sanctum.interfaces.isolation.IsolationSession;
import fr.anisekai.web.AuthenticationManager;
import fr.anisekai.web.annotations.RequireAuth;
import fr.anisekai.web.annotations.RequireIsolation;
import fr.anisekai.web.data.Session;
import fr.anisekai.web.data.SessionToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class IsolationInterceptor implements HandlerInterceptor {


    private static final Logger LOGGER = LoggerFactory.getLogger(IsolationInterceptor.class);

    private final AuthenticationManager manager;

    public IsolationInterceptor(AuthenticationManager manager) {

        this.manager = manager;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod method)) {
            return true; // Not a controller method — allow
        }

        if (!method.hasMethodAnnotation(RequireIsolation.class)) {
            return true; // Does not have an isolation requirement.
        }

        String route = String.format("%s %s", request.getMethod(), request.getRequestURI());

        if (!method.hasMethodAnnotation(RequireAuth.class)) {
            LOGGER.error("[{}] <!> @RequireIsolation used without @RequireAuth", route);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Misconfiguration");
            return false;
        }


        Session session = (Session) request.getAttribute("session");

        // Previous interceptor (authentication) should have handled this, but better safe than sorry.
        if (session == null) {
            LOGGER.warn("[{}] Can't provide isolation context: No session available", route);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "IsolationSession not available right now.");
            return false;
        }

        SessionToken token = session.getToken();
        Optional<IsolationSession> isolationSession = this.manager.resolveIsolation(token);

        if (isolationSession.isEmpty()) {
            LOGGER.warn("[{}] Can't provide isolation context: No isolation available", route);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Your session does not have any isolation session attached.");
            return false;
        }

        request.setAttribute("isolation", isolationSession.get());
        return true;
    }

}
