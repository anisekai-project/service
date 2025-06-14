package fr.anisekai.web.configs;

import fr.anisekai.web.oauth2.OAuth2TokenClient;
import fr.anisekai.web.oauth2.OAuth2UserProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> tokenClient  = new OAuth2TokenClient(this.restOperations());
        OAuth2UserService<OAuth2UserRequest, OAuth2User>                     userProvider = new OAuth2UserProvider(this.restOperations());

        return httpSecurity
                // 🔐 Allow anonymous access to API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v3/**", "/error", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                // ❌ CSRF is useless for APIs
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/v3/**") // just disable it for your API
                )
                // 🌐 OAuth2 login for web browser
                .oauth2Login(configurer -> configurer
                        .tokenEndpoint(config -> config.accessTokenResponseClient(tokenClient))
                        .userInfoEndpoint(config -> config.userService(userProvider))
                        .loginPage("/login")
                        .failureUrl("/")
                        .defaultSuccessUrl("/", true)
                ).build();
    }

    @Bean
    public RestOperations restOperations() {

        return new RestTemplate();
    }

}
