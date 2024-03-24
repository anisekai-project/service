package me.anisekai.modules.freya.entities.detached.disk;


import me.anisekai.modules.toshiko.Texts;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class DiskGroup {

    private final UUID             uuid;
    private final String           name;
    private final Set<DiskEpisode> episodes;

    public DiskGroup(File file) {

        this.uuid     = UUID.randomUUID();
        this.name     = Texts.unslugify(file.getName());
        this.episodes = new TreeSet<>(Comparator.comparing(DiskEpisode::getName));
    }

    public UUID getUuid() {

        return this.uuid;
    }

    public String getName() {

        return this.name;
    }

    public void finalize(CharSequence animeFsRoot, CharSequence subtitleFsRoot) {

        this.episodes.forEach(f -> f.finalize(animeFsRoot, subtitleFsRoot));
    }

    public void add(DiskEpisode diskEpisode) {

        this.episodes.add(diskEpisode);
    }

    public Set<DiskEpisode> getEpisodes() {

        return this.episodes;
    }

}
