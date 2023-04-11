package me.anisekai.toshiko.services;

import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.utils.FileSystemUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Duration;

@Service
public class StorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);

    @Value("${toshiko.fs.root}")
    private String filesystemRootPath;

    private JSONArray database;

    public StorageService() {

        this.database = new JSONArray();
    }

    public JSONArray getDatabase() {

        return this.database;
    }

    public void cache() {
        // Build the cache.
        JSONArray database = new JSONArray();

        for (File anime : FileSystemUtils.files(this.filesystemRootPath)) {

            if (anime.isFile()) {
                continue;
            }

            JSONObject animeJson = new JSONObject();
            animeJson.put("name", Texts.unslugify(anime.getName()));

            JSONArray groupArr = new JSONArray();

            for (File group : FileSystemUtils.files(anime)) {

                JSONObject groupJson = new JSONObject();

                if (group.isFile()) {

                    if (group.getName().endsWith(".ass")) {
                        LOGGER.info("Ignore subtitle file @ {}", group.getAbsolutePath());
                        continue;
                    }

                    int    extensionIndex = group.getName().lastIndexOf(".");
                    String groupName      = group.getName().substring(0, extensionIndex);
                    groupJson.put("name", Texts.unslugify(groupName));
                    groupJson.put("uri", String.format("https://toshiko.alexpado.fr/animes/%s/%s", anime.getName(), group.getName()));
                } else {
                    groupJson.put("name", Texts.unslugify(group.getName()));
                    JSONArray episodeArr = new JSONArray();
                    for (File episode : FileSystemUtils.files(group)) {

                        if (episode.isDirectory() || episode.getName().endsWith(".ass")) {
                            continue;
                        }

                        JSONObject episodeJson = new JSONObject();

                        int extensionIndex = episode.getName().lastIndexOf(".");

                        if (extensionIndex == -1) {
                            LOGGER.warn("Could not determine file extension for {}", episode.getAbsolutePath());
                            continue;
                        }
                        String episodeName = episode.getName().substring(0, extensionIndex);

                        episodeJson.put("name", episodeName);
                        episodeJson.put("uri", String.format("https://toshiko.alexpado.fr/animes/%s/%s/%s", anime.getName(), group.getName(), episode.getName()));
                        episodeArr.put(episodeJson);
                    }
                    groupJson.put("episodes", episodeArr);
                }

                groupArr.put(groupJson);
            }

            animeJson.put("groups", groupArr);
            database.put(animeJson);
        }

        // Commit cache
        this.database = database;
    }


}
