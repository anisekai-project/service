package fr.anisekai.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApiKeyData(
        @Schema(description = "The API Key.")
        String key
) {

}
