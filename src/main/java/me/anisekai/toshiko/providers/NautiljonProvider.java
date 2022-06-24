package me.anisekai.toshiko.providers;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import me.anisekai.toshiko.enums.PublicationState;
import me.anisekai.toshiko.exceptions.providers.StupidUserException;
import me.anisekai.toshiko.helpers.JsoupHelper;
import me.anisekai.toshiko.interfaces.AnimeProvider;
import me.anisekai.toshiko.utils.FailSafeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.Set;

public class NautiljonProvider implements AnimeProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(NautiljonProvider.class);

    private final String           name;
    private final String           synopsis;
    private final String           url;
    private final String           image;
    private final PublicationState publicationState;
    private final Set<String>      tags;
    private final Integer          episodeCount;
    private final Double           rating;

    public NautiljonProvider(URI uri) throws IOException {

        Document    document = Jsoup.connect(uri.toString()).get();

        if (uri.getPath().startsWith("/mangas/")) {
            throw new StupidUserException();
        }

        JsoupHelper helper   = new JsoupHelper(document);

        this.name         = helper.readMetaContent("og:title").orElse("*No title*");
        this.synopsis     = helper.readClassContent("description").orElse("*No description available*");
        this.url          = uri.toString();
        this.image        = helper.readMetaContent("og:image").orElse(null);
        this.tags         = helper.readPropertiesContent("genre");
        this.episodeCount = helper.readPropertyContent("numberOfEpisodes").map(FailSafeUtils::parseInt).orElse(null);
        this.rating       = helper.readPropertyContent("ratingValue").map(FailSafeUtils::parseDouble).orElse(null);

        Elements els = document.select("#content [itemprop='datePublished']");

        if (els.isEmpty()) {
            this.publicationState = PublicationState.UNAVAILABLE;
            return;
        }

        Element el = els.get(0);

        if (el.parent() == null) {
            this.publicationState = PublicationState.UNAVAILABLE;
            return;
        }

        String text = el.parent().text();

        if (text.contains("Diffusion terminée")) {
            this.publicationState = PublicationState.FINISHED;
        } else if (text.contains("en cours")) {
            this.publicationState = PublicationState.AIRING;
        } else {
            this.publicationState = PublicationState.UNAVAILABLE;
        }
    }

    @Override
    public String getName() {

        return this.name;
    }

    @Override
    public String getSynopsis() {

        return this.synopsis;
    }

    @Override
    public String getUrl() {

        return this.url;
    }

    @Override
    public String getImage() {

        return this.image;
    }

    @Override
    public PublicationState getPublicationState() {

        return this.publicationState;
    }

    @Override
    public Set<String> getTags() {

        return this.tags;
    }

    @Override
    public Optional<Integer> getEpisodeCount() {

        return Optional.ofNullable(this.episodeCount);
    }

    @Override
    public Optional<Double> getRating() {

        return Optional.ofNullable(this.rating);
    }

}
