package me.anisekai.toshiko.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "toshiko.features")
public class ToshikoFeatureConfiguration {

    /**
     * Define if the application should enable the discord bot related features.
     */
    private boolean botEnabled;

    /**
     * Define if the application should enable to disk related features.
     */
    private boolean diskEnabled;


    public boolean isBotEnabled() {

        return this.botEnabled;
    }

    public void setBotEnabled(boolean botEnabled) {

        this.botEnabled = botEnabled;
    }

    public boolean isDiskEnabled() {

        return this.diskEnabled;
    }

    public void setDiskEnabled(boolean diskEnabled) {

        this.diskEnabled = diskEnabled;
    }
}
