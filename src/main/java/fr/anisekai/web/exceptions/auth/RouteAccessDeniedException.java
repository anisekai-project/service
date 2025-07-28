package fr.anisekai.web.exceptions.auth;

import fr.anisekai.web.exceptions.WebException;
import fr.anisekai.wireless.remote.interfaces.UserEntity;
import org.springframework.http.HttpStatus;

public class RouteAccessDeniedException extends WebException {

    public RouteAccessDeniedException(String route, UserEntity user) {

        super(
                HttpStatus.FORBIDDEN,
                "Access to route [%s] for user [%s] has been denied.".formatted(route, user.getId()),
                "Access to resource forbidden."
        );
    }

}
