package fr.anisekai.library;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "disk")
public class LibraryConfiguration {

    private String media;

    public String getMedia() {

        return this.media;
    }

    public void setMedia(String media) {

        this.media = media;
    }

    public Path getLocation() {

        return Path.of(this.getMedia());
    }

}
