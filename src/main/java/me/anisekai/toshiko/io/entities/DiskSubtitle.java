package me.anisekai.toshiko.io.entities;

import java.io.File;
import java.util.UUID;

public class DiskSubtitle {

    private final UUID uuid;
    private final File path;

    public DiskSubtitle(File path) {

        this.uuid = UUID.randomUUID();
        this.path = path;
    }

    public UUID getUuid() {

        return this.uuid;
    }

    public File getPath() {

        return this.path;
    }

}
