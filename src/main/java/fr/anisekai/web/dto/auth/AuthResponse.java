package fr.anisekai.web.dto.auth;

import fr.anisekai.web.data.Session;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthResponse", description = "Represent the result of an authentication request")
public record AuthResponse(
        @Schema(description = "The session token to use in the authorization header", example = "eyJ0eXBlIjoib2F1dGgiLCJrZXkiOiJIZWxsbywgV29ybGQifQ==")
        String token,
        @Schema(description = "The user that has logged in")
        UserDto user
) {

    public static AuthResponse of(Session session) {

        return new AuthResponse(
                session.getToken().toBase64(),
                UserDto.of(session.getIdentity())
        );
    }

}
