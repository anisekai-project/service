package me.anisekai.toshiko.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "toshiko.auto-download")
public class AutoDownloadConfiguration {

    /**
     * URL pointing to the RSS feed.
     */
    private String rss;

    /**
     * URL pointing to the transmission-daemon RPC API.
     */
    private String rpc;


    public String getRss() {

        return this.rss;
    }

    public void setRss(String rss) {

        this.rss = rss;
    }

    public String getRpc() {

        return this.rpc;
    }

    public void setRpc(String rpc) {

        this.rpc = rpc;
    }

}
