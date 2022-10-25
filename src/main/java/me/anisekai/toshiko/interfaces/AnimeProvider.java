package me.anisekai.toshiko.interfaces;

import me.anisekai.toshiko.enums.PublicationState;
import me.anisekai.toshiko.exceptions.providers.InvalidLinkException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;

public interface AnimeProvider {

    static boolean isSupported(String link) {

        try {
            return isSupported(new URI(link));
        } catch (URISyntaxException e) {
            throw new InvalidLinkException(link);
        }
    }

    static boolean isSupported(URI uri) {

        return switch (uri.getHost()) {
            case "nautiljon.com", "www.nautiljon.com" -> true;
            default -> false;
        };
    }

    String getName();

    String getSynopsis();

    String getUrl();

    String getImage();

    PublicationState getPublicationState();

    Set<String> getGenres();

    Set<String> getThemes();

    Optional<Long> getEpisodeCount();

    Optional<Double> getRating();

}
