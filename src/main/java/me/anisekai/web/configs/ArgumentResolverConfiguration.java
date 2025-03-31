package me.anisekai.web.configs;

import me.anisekai.server.services.DiscordUserService;
import me.anisekai.web.resolvers.DiscordUserResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
public class ArgumentResolverConfiguration implements WebMvcConfigurer {

    private final DiscordUserService discordUserService;

    public ArgumentResolverConfiguration(DiscordUserService discordUserService) {

        this.discordUserService = discordUserService;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        resolvers.add(new DiscordUserResolver(this.discordUserService));
    }

}
