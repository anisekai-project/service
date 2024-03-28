package me.anisekai.modules.freya.entities.detached.disk;


import me.anisekai.modules.toshiko.Texts;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class DiskAnime implements Comparable<DiskAnime> {

    private final UUID             uuid;
    private final File             file;
    private final String           name;
    private final Set<DiskGroup>   groups;
    private final Set<DiskEpisode> episodes;

    public DiskAnime(File file) {

        this.uuid     = UUID.randomUUID();
        this.file     = file;
        this.name     = Texts.unslugify(file.getName());
        this.groups   = new TreeSet<>(Comparator.comparing(DiskGroup::getName));
        this.episodes = new TreeSet<>(Comparator.comparing(DiskEpisode::getName));
    }

    public File getFile() {

        return this.file;
    }

    public UUID getUuid() {

        return this.uuid;
    }

    public String getName() {

        return this.name;
    }

    public void add(DiskGroup diskGroup) {

        this.groups.add(diskGroup);
    }

    public void add(DiskEpisode diskEpisode) {

        this.episodes.add(diskEpisode);
    }

    public Set<DiskGroup> getGroups() {

        return this.groups;
    }

    public Set<DiskEpisode> getEpisodes() {

        return this.episodes;
    }

    public void finalize(CharSequence animeFsRoot, CharSequence subtitleFsRoot) {

        this.groups.forEach(g -> g.finalize(animeFsRoot, subtitleFsRoot));
        this.episodes.forEach(f -> f.finalize(animeFsRoot, subtitleFsRoot));
    }

    @Override
    public int compareTo(@NotNull DiskAnime o) {

        return this.getName().compareTo(o.getName());
    }

}
