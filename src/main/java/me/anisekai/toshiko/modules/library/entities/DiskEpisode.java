package me.anisekai.toshiko.modules.library.entities;

import me.anisekai.toshiko.modules.library.utils.FileSystemUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DiskEpisode {

    private final UUID              uuid;
    private final File              path;
    private final String            name;
    private final Set<DiskSubtitle> subtitles;

    public DiskEpisode(File file) {

        this.uuid      = UUID.randomUUID();
        this.path      = file;
        this.name      = this.getPath().getName().substring(0, this.getPath().getName().lastIndexOf('.'));
        this.subtitles = new HashSet<>();
    }

    public UUID getUuid() {

        return this.uuid;
    }

    public File getPath() {

        return this.path;
    }

    public String getName() {

        return this.name;
    }

    public Set<DiskSubtitle> getSubtitles() {

        return this.subtitles;
    }

    public void finalize(CharSequence animeFsRoot, CharSequence subtitleFsRoot) {

        String subtitleDirectory = this.getPath().getAbsolutePath()
                                       .replace(this.path.getName(), "")
                                       .replace(animeFsRoot, subtitleFsRoot);

        String filter = this.getPath().getName().substring(0, this.getPath().getName().lastIndexOf('.'));

        // Find
        File subtitlesDirectoryFile = new File(subtitleDirectory);

        if (subtitlesDirectoryFile.exists()) {
            FileSystemUtils.files(subtitleDirectory).stream()
                           .filter(file -> file.getName().startsWith(filter))
                           .map(DiskSubtitle::new)
                           .forEach(this.subtitles::add);
        }
    }

}
