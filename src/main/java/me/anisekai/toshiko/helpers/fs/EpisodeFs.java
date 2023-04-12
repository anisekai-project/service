package me.anisekai.toshiko.helpers.fs;

import me.anisekai.toshiko.services.StorageService;
import me.anisekai.toshiko.utils.FileSystemUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EpisodeFs {

    private final UUID            uuid;
    private final File            path;
    private final String          name;
    private       String          uri;
    private final Set<SubtitleFs> subtitles;

    public EpisodeFs(File file) {

        this.uuid      = UUID.randomUUID();
        this.path      = file;
        this.name      = this.getPath().getName().substring(0, this.getPath().getName().lastIndexOf('.'));
        this.subtitles = new HashSet<>();
    }

    public File getPath() {

        return this.path;
    }

    public String getName() {

        return this.name;
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
                           .map(SubtitleFs::new)
                           .forEach(this.subtitles::add);

            this.subtitles.forEach(s -> s.finalize(subtitleFsRoot));
        }

        this.uri = this.getPath().getAbsolutePath().replace(animeFsRoot, StorageService.HTTP_ANIME_ROOT)
                       .replace("\\", "/");
    }

    public JSONObject toJson() {

        JSONArray subtitles = new JSONArray();
        this.subtitles.stream().map(SubtitleFs::toJson).forEach(subtitles::put);

        JSONObject json = new JSONObject();
        json.put("id", this.uuid.toString());
        json.put("name", this.name);
        json.put("uri", this.uri);
        json.put("subtitles", subtitles);
        return json;
    }
}
