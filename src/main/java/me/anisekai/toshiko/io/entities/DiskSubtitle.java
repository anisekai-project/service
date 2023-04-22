package me.anisekai.toshiko.io.entities;

import me.anisekai.toshiko.io.DiskService;
import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

public class DiskSubtitle {

    private final UUID   uuid;
    private final File   path;
    private       String uri;

    public DiskSubtitle(File path) {

        this.uuid = UUID.randomUUID();
        this.path = path;
    }

    public File getPath() {

        return this.path;
    }

    public String getUri() {

        return this.uri;
    }

    public void finalize(CharSequence subtitleFsRoot) {

        this.uri = this.path.getAbsolutePath().replace(subtitleFsRoot, DiskService.HTTP_SUBS_ROOT)
                            .replace("\\", "/");
    }

    public JSONObject toJson() {

        JSONObject json = new JSONObject();
        json.put("id", this.uuid.toString());
        json.put("uri", this.uri);
        return json;
    }
}
