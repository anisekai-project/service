package fr.anisekai.web.oauth2;

import fr.anisekai.toshiko.BuildInfo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.web.client.RestOperations;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class OAuth2UserProvider implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final RestOperations restOperations;

    public OAuth2UserProvider(RestOperations restOperations) {

        this.restOperations = restOperations;
    }

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        String userInfoUrl = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set(
                HttpHeaders.AUTHORIZATION,
                String.format("Bearer %s", userRequest.getAccessToken().getTokenValue())
        );
        headers.set(HttpHeaders.USER_AGENT, String.format("anisekai/%s", BuildInfo.getVersion()));

        ParameterizedTypeReference<Map<String, Object>> typeReference = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<Map<String, Object>> responseEntity = this.restOperations.exchange(
                userInfoUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                typeReference
        );

        Map<String, Object>   userAttributes = responseEntity.getBody();
        Set<GrantedAuthority> authorities    = Collections.singleton(new OAuth2UserAuthority(userAttributes));

        assert userAttributes != null;
        return new OAuth2DiscordUser(authorities, userAttributes);
    }

}
