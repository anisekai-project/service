package me.anisekai.toshiko.modules.library.entities;

import me.anisekai.toshiko.modules.discord.Texts;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class DiskAnime {

    private final UUID             uuid;
    private final String           name;
    private final Set<DiskGroup>   groups;
    private final Set<DiskEpisode> episodes;

    public DiskAnime(File file) {

        this.uuid     = UUID.randomUUID();
        this.name     = Texts.unslugify(file.getName());
        this.groups   = new TreeSet<>(Comparator.comparing(DiskGroup::getName));
        this.episodes = new TreeSet<>(Comparator.comparing(DiskEpisode::getName));
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

}
