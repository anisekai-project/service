package me.anisekai.toshiko.modules.web.resolvers;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.modules.web.oauth2.OAuth2DiscordUser;
import me.anisekai.toshiko.repositories.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserResolver implements HandlerMethodArgumentResolver {

    private final UserRepository repository;

    public UserResolver(UserRepository repository) {

        this.repository = repository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.getParameter().getType() == DiscordUser.class;
    }

    @Override
    public Object resolveArgument(
            @NotNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NotNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof OAuth2DiscordUser user) {
            DiscordUser discordUser = this.repository.findById(user.getIdLong()).orElseGet(() -> {
                DiscordUser newUser = new DiscordUser();
                newUser.setId(user.getIdLong());
                newUser.setUsername(user.getUsername());
                newUser.setDiscriminator("0000");
                newUser.setActive(false);
                newUser.setAdmin(false);
                newUser.setWebAccess(false);
                return newUser;
            });

            discordUser.setId(user.getIdLong());
            discordUser.setUsername(user.getUsername());
            discordUser.setDiscriminator("0000");

            return this.repository.save(discordUser);
        }

        return null;
    }

}
