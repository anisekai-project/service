package fr.anisekai.web.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.Map;

public final class WebException extends RuntimeException {

    public static final int AUTHORIZATION_MISSING      = 10100; // Authorization header was not provided.
    public static final int AUTHORIZATION_INVALID      = 10101; // Authorization header was provided but incorrect.
    public static final int AUTHORIZATION_NO_SESSION   = 10102; // Authorization header was provided but incorrect.
    public static final int AUTHENTICATION_FAILURE     = 10103; // Could not exchange the code for a token (bad code).
    public static final int AUTHENTICATION_UNAVAILABLE = 10104; // Unable to exchange the code for a token (connectivity).
    public static final int AUTHORIZATION_PERMISSION   = 10201; // Authenticated but not allowed.

    private static final Map<Integer, HttpStatus> ERROR_STATUS_MAP = Map.ofEntries(
            Map.entry(AUTHORIZATION_MISSING, HttpStatus.UNAUTHORIZED),
            Map.entry(AUTHORIZATION_INVALID, HttpStatus.UNAUTHORIZED),
            Map.entry(AUTHORIZATION_NO_SESSION, HttpStatus.UNAUTHORIZED),
            Map.entry(AUTHENTICATION_FAILURE, HttpStatus.UNAUTHORIZED),
            Map.entry(AUTHENTICATION_UNAVAILABLE, HttpStatus.BAD_GATEWAY),
            Map.entry(AUTHORIZATION_PERMISSION, HttpStatus.FORBIDDEN)
    );

    public static WebException ofInternalCode(int code) {

        HttpStatus httpStatus = ERROR_STATUS_MAP.getOrDefault(code, HttpStatus.INTERNAL_SERVER_ERROR);
        return new WebException(httpStatus, code);
    }

    public static WebException ofInternalCode(int code, Throwable cause) {

        HttpStatus httpStatus = ERROR_STATUS_MAP.getOrDefault(code, HttpStatus.INTERNAL_SERVER_ERROR);
        return new WebException(cause, httpStatus, code);
    }

    @Schema(name = "WebException", description = "Used when something isn't going how it is supposed to go.")
    public record Dto(
            @Schema(description = "Time at which the error has been generated.")
            ZonedDateTime timestamp,
            @Schema(description = "Unique code for this error (one error code per reason).", minimum = "10000", maximum = "19999")
            int code,
            @Schema(description = "Status code for the error.", minimum = "100", maximum = "511")
            int status,
            @Schema(description = "Human-readable name for the status code.")
            String error
    ) {

        public static Dto from(WebException ex) {

            return new Dto(ex.timestamp, ex.code, ex.status, ex.error);
        }

    }

    public final ZonedDateTime timestamp;
    public final int           code;
    public final int           status;
    public final String        error;

    private WebException(HttpStatus status, int code) {
        this(null, status, code);
    }

    private WebException(Throwable cause, HttpStatus status, int code) {
        super(cause);
        this.timestamp = ZonedDateTime.now();
        this.code = code;
        this.status = status.value();
        this.error = status.getReasonPhrase();
    }

}
