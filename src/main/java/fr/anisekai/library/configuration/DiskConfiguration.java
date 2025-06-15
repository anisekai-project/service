package fr.anisekai.library.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@ConfigurationProperties(prefix = "disk")
public class DiskConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskConfiguration.class);

    private String media;

    public String getMedia() {

        return this.media;
    }

    public void setMedia(String media) {

        this.media = media;
    }

}
