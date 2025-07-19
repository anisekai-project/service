package fr.anisekai.web.resolvers;

import fr.anisekai.web.AuthenticationManager;
import fr.anisekai.web.data.Session;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class SessionArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionArgumentResolver.class);

    private final AuthenticationManager manager;

    public SessionArgumentResolver(AuthenticationManager manager) {

        this.manager = manager;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.getParameterType().equals(Session.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {


        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return null;
        }

        Session session = (Session) request.getAttribute("session");

        if (session == null) {
            LOGGER.warn("Tried to resolve Session parameter, but no session was available in request attributes.");
            return null;
        }

        return session;
    }

}
