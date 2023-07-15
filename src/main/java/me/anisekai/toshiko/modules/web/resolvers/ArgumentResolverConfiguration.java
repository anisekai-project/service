package me.anisekai.toshiko.modules.web.resolvers;

import me.anisekai.toshiko.repositories.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
public class ArgumentResolverConfiguration implements WebMvcConfigurer {

    private final UserRepository repository;

    public ArgumentResolverConfiguration(UserRepository repository) {

        this.repository = repository;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        resolvers.add(new UserResolver(this.repository));
    }

}
