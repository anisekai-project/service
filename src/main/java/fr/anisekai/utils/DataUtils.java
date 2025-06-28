package fr.anisekai.utils;

import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Track;
import fr.anisekai.wireless.api.json.AnisekaiArray;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import org.jetbrains.annotations.NotNull;

public class DataUtils {

    public static @NotNull AnisekaiArray getTracksArray(Episode episode) {

        AnisekaiArray tracks = new AnisekaiArray();
        for (Track track : episode.getTracks()) {
            AnisekaiJson trackJson = new AnisekaiJson();
            trackJson.put("id", track.getId());
            trackJson.put("name", track.getName());
            trackJson.put("label", track.getLabel());
            trackJson.put("codec", track.getCodec().name().toLowerCase());
            trackJson.put("type", track.getCodec().getType().name().toLowerCase());
            trackJson.put("language", track.getLanguage());
            trackJson.put("forced", track.isForced());

            tracks.put(trackJson);
        }
        return tracks;
    }

}
