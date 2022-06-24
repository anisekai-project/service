package me.anisekai.toshiko.interfaces;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import me.anisekai.toshiko.enums.PublicationState;
import me.anisekai.toshiko.exceptions.providers.UnsupportedAnimeProviderException;
import me.anisekai.toshiko.exceptions.providers.InvalidLinkException;
import me.anisekai.toshiko.exceptions.providers.ProviderLoadingException;
import me.anisekai.toshiko.providers.NautiljonProvider;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;

public interface AnimeProvider {

    static AnimeProvider of(String link) {

        try {
            return of(new URI(link));
        } catch (URISyntaxException e) {
            throw new InvalidLinkException(link);
        }

    }

    static AnimeProvider of(URI uri) {
        try {
            return switch (uri.getHost()) {
                case "nautiljon.com", "www.nautiljon.com" -> new NautiljonProvider(uri);
                default -> throw new UnsupportedAnimeProviderException(uri);
            };
        } catch (IOException e) {
            throw new ProviderLoadingException(e);
        }
    }

    String getName();

    String getSynopsis();

    String getUrl();

    String getImage();

    PublicationState getPublicationState();

    Set<String> getTags();

    Optional<Integer> getEpisodeCount();

    Optional<Double> getRating();

}
