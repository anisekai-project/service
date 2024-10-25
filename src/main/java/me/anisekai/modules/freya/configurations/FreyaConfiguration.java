package me.anisekai.modules.freya.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "anisekai.freya")
public class FreyaConfiguration {

    /**
     * Absolute path pointing to the root folder of the library.
     */
    private String library;

    /**
     * URL pointing to the transmission-daemon RPC API.
     */
    private String rpc;

    /**
     * URL pointing to the Nyaa's user RSS feed.
     */
    private String rss;

    /**
     * Define if the automatic download of anime should be enabled
     */
    private Boolean autoDownloadEnabled;

    /**
     * Define if the automatic scan of the library content should be enabled.
     */
    private Boolean libraryScanEnabled;

    public String getLibrary() {

        return this.library;
    }

    public void setLibrary(String library) {

        this.library = library;
    }

    public String getRpc() {

        return this.rpc;
    }

    public void setRpc(String rpc) {

        this.rpc = rpc;
    }

    public String getRss() {

        return this.rss;
    }

    public void setRss(String rss) {

        this.rss = rss;
    }

    public boolean isAutoDownloadEnabled() {

        return this.autoDownloadEnabled != null && this.autoDownloadEnabled;
    }

    public void setAutoDownloadEnabled(Boolean autoDownloadEnabled) {

        this.autoDownloadEnabled = autoDownloadEnabled;
    }

    public boolean isLibraryScanEnabled() {

        return this.libraryScanEnabled != null && this.libraryScanEnabled;
    }

    public void setLibraryScanEnabled(Boolean libraryScanEnabled) {

        this.libraryScanEnabled = libraryScanEnabled;
    }

}
