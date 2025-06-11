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

    private String downloads;
    private String media;

    public String getDownloads() {

        return this.downloads;
    }

    public void setDownloads(String downloads) {

        this.downloads = downloads;
    }

    public String getMedia() {

        return this.media;
    }

    public void setMedia(String media) {

        this.media = media;
    }

    private File getFile(String path, String name) {

        if (!path.startsWith("/")) {
            LOGGER.warn("Path '{}' is not an absolute path. This is not recommended.", name);
        }
        return new File(path);
    }

    public File getDownloadFile() {

        return this.getFile(this.downloads, "downloads");
    }

    public File getMediaFile() {

        return this.getFile(this.media, "media");
    }

}
