package fr.anisekai.web.configs;

import fr.anisekai.server.services.UserService;
import fr.anisekai.web.resolvers.DiscordUserResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
public class UserHandlerResolver implements WebMvcConfigurer {

    private final UserService userService;

    public UserHandlerResolver(UserService userService) {

        this.userService = userService;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        resolvers.add(new DiscordUserResolver(this.userService));
    }

}
