package me.anisekai.modules.freya;

import me.anisekai.modules.freya.configurations.FreyaConfiguration;

import java.io.File;

public class StorageManager {

    private final FreyaConfiguration configuration;

    private final File contentPath;
    private final File automationPath;
    private final File torrentPath;

    public StorageManager(FreyaConfiguration configuration) {

        this.configuration = configuration;

        this.contentPath    = new File(this.configuration.getLibrary(), "library");
        this.automationPath = new File(this.configuration.getLibrary(), "automation");
        this.torrentPath    = new File(this.configuration.getLibrary(), "torrents");
    }

    public void checkDisk() {


    }

}
