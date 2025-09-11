package fr.anisekai.utils;

import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Track;
import fr.anisekai.wireless.api.json.AnisekaiArray;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.api.media.enums.Disposition;
import org.jetbrains.annotations.NotNull;

public final class DataUtils {

    private DataUtils() {}

    public static @NotNull AnisekaiArray getTracksArray(Episode episode) {

        AnisekaiArray tracks = new AnisekaiArray();
        for (Track track : episode.getTracks()) {
            AnisekaiJson trackJson = new AnisekaiJson();
            trackJson.put("id", track.getId());
            trackJson.put("name", track.getName());
            trackJson.put("codec", track.getCodec().name().toLowerCase());
            trackJson.put("type", track.getCodec().getType().name().toLowerCase());
            trackJson.put("language", track.getLanguage());

            trackJson.put(
                    "dispositions",
                    Disposition.fromBits(track.getDispositions()).stream().map(Disposition::name).toList()
            );

            tracks.put(trackJson);
        }
        return tracks;
    }

}
