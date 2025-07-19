package fr.anisekai.web.api;


import fr.alexpado.lib.rest.exceptions.RestException;
import fr.anisekai.web.AuthenticationManager;
import fr.anisekai.web.annotations.RequireAuth;
import fr.anisekai.web.data.AuthenticationKey;
import fr.anisekai.web.data.Session;
import fr.anisekai.web.dto.auth.AuthResponse;
import fr.anisekai.web.dto.auth.AuthRequest;
import fr.anisekai.web.dto.auth.UserDto;
import fr.anisekai.web.enums.SessionType;
import fr.anisekai.web.exceptions.WebException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v3/auth")
public class AuthenticationController {

    private final static Logger                LOGGER = LoggerFactory.getLogger(AuthenticationController.class);
    private final        AuthenticationManager manager;

    public AuthenticationController(AuthenticationManager manager) {

        this.manager = manager;
    }

    @PostMapping(value = "/code", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Authenticate with Discord", description = "Use the code provided to try and authenticate with Discord servers.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful.", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failure.", content = @Content(schema = @Schema(implementation = WebException.Dto.class))),
            @ApiResponse(responseCode = "500", description = "Authentication error.", content = @Content(schema = @Schema(implementation = WebException.Dto.class)))
    })
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {

        try {
            AuthenticationKey key = AuthenticationKey.fromBase64(request.key());
            Session           session = this.manager.authenticate(key);
            return ResponseEntity.ok(AuthResponse.of(session));
        } catch (Exception ex) {
            LOGGER.error("Unable to authenticate user", ex);
            if (ex instanceof RestException rex) {
                LOGGER.debug("Feedback: {}", new String(rex.getBody()));
                if (rex.getCode() >= 400 && rex.getCode() < 500) {
                    throw WebException.ofInternalCode(WebException.AUTHENTICATION_FAILURE);
                }
            }
            throw WebException.ofInternalCode(WebException.AUTHENTICATION_UNAVAILABLE);
        }
    }

    @PostMapping(value = "/application", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Authenticate with an API key", description = "Use the API key provided to exchange it for a session token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful.", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failure.", content = @Content(schema = @Schema(implementation = WebException.Dto.class))),
            @ApiResponse(responseCode = "500", description = "Authentication error.", content = @Content(schema = @Schema(implementation = WebException.Dto.class)))
    })
    public ResponseEntity<AuthResponse> exchange(@RequestBody AuthRequest request) {
        try {
            AuthenticationKey key = AuthenticationKey.fromBase64(request.key());
            Session           session = this.manager.exchange(key);
            return ResponseEntity.ok(AuthResponse.of(session));
        } catch (Exception ex) {
            LOGGER.error("Unable to authenticate application", ex);
            throw WebException.ofInternalCode(WebException.AUTHENTICATION_FAILURE);
        }
    }

    @RequireAuth
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Check session", description = "Allow to check if the authorization header still lead to a valid session.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The session is valid.", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "The session is not valid.")
    })
    public ResponseEntity<UserDto> checkSession(Session session) {

        return ResponseEntity.ok(UserDto.of(session.getIdentity()));
    }

}
