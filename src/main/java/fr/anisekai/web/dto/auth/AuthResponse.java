package fr.anisekai.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthResponse", description = "Represent the result of an authentication request")
public record AuthResponse(
        @Schema(description = "The session token to use in the authorization header.")
        String accessToken,
        @Schema(description = "The refresh token to use to obtain a new access token.")
        String refreshToken
) {

}
