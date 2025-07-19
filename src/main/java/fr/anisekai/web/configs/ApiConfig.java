package fr.anisekai.web.configs;

import fr.anisekai.web.interceptors.AuthenticationInterceptor;
import fr.anisekai.web.interceptors.IsolationInterceptor;
import fr.anisekai.web.resolvers.SessionArgumentResolver;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class ApiConfig implements WebMvcConfigurer {

    private final SessionArgumentResolver   sessionArgumentResolver;
    private final AuthenticationInterceptor authenticationInterceptor;
    private final IsolationInterceptor      isolationInterceptor;
    private final WebConfig                 config;

    public ApiConfig(SessionArgumentResolver sessionArgumentResolver, AuthenticationInterceptor authenticationInterceptor, IsolationInterceptor isolationInterceptor, WebConfig config) {

        this.sessionArgumentResolver   = sessionArgumentResolver;
        this.authenticationInterceptor = authenticationInterceptor;
        this.isolationInterceptor      = isolationInterceptor;
        this.config                    = config;
    }

    @Bean
    public RestOperations restOperations() {

        return new RestTemplate();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {

                registry.addMapping("/**") // Allow all endpoints
                        .allowedOrigins(ApiConfig.this.config.getHost()) // Allow frontend origin
                        .allowedMethods("*") // GET, POST, etc.
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(this.authenticationInterceptor)
                .addPathPatterns("/api/v3/**");

        registry.addInterceptor(this.isolationInterceptor)
                .addPathPatterns("/api/v3/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        resolvers.add(this.sessionArgumentResolver);
    }

}
