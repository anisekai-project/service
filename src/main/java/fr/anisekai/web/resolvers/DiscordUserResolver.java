package fr.anisekai.web.resolvers;

import fr.anisekai.wireless.remote.interfaces.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.services.UserService;
import fr.anisekai.web.oauth2.OAuth2DiscordUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

public class DiscordUserResolver implements HandlerMethodArgumentResolver {

    private final UserService service;

    public DiscordUserResolver(UserService service) {

        this.service = service;
    }

    /**
     * Whether the given {@linkplain MethodParameter method parameter} is supported by this resolver.
     *
     * @param parameter
     *         the method parameter to check
     *
     * @return {@code true} if this resolver supports the supplied parameter; {@code false} otherwise
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return Arrays.asList(
                DiscordUser.class,
                UserEntity.class
        ).contains(parameter.getParameter().getType());
    }

    /**
     * Resolves a method parameter into an argument value from a given request. A {@link ModelAndViewContainer} provides
     * access to the model for the request. A {@link WebDataBinderFactory} provides a way to create a
     * {@link WebDataBinder} instance when needed for data binding and type conversion purposes.
     *
     * @param parameter
     *         the method parameter to resolve. This parameter must have previously been passed to
     *         {@link #supportsParameter} which must have returned {@code true}.
     * @param mavContainer
     *         the ModelAndViewContainer for the current request
     * @param webRequest
     *         the current request
     * @param binderFactory
     *         a factory for creating {@link WebDataBinder} instances
     *
     * @return the resolved argument value, or {@code null} if not resolvable
     */
    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof OAuth2DiscordUser user) {
            // Do not create a DiscordUser if we don't have it, otherwise update the existing one.
            return this.service.getProxy().fetchEntity(user.getIdLong())
                               .map(localUser -> this.service.getProxy().modify(
                                       localUser,
                                       proxy -> proxy.setUsername(user.getUsername())
                               ))
                               .orElse(null);
        }

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String             path    = request.getRequestURI();

        if (path.startsWith("/api/v3")) {
            String apiKey = request.getHeader("X-API-KEY");
            if (apiKey == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing API Key");
            }

            return this.service.getByApiKey(apiKey)
                               .orElseThrow(() -> new ResponseStatusException(
                                       HttpStatus.UNAUTHORIZED,
                                       "Wrong API Key"
                               ));
        }

        return null;
    }

}
