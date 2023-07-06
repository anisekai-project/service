package me.anisekai.toshiko.helpers;

import fr.alexpado.lib.rest.RestAction;
import fr.alexpado.lib.rest.enums.RequestMethod;
import fr.alexpado.lib.rest.interfaces.IRestAction;
import fr.alexpado.lib.rest.interfaces.IRestResponse;
import me.anisekai.toshiko.entities.Anime;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class FileDownloader extends RestAction<byte[]> {

    private static final Map<Anime, byte[]> FILE_CACHE = new HashMap<>();
    private final        String             url;

    public FileDownloader(String url) {

        this.url = url;
    }

    public static byte[] downloadAnimeCard(Anime anime) throws Exception {

        if (FILE_CACHE.containsKey(anime)) {
            return FILE_CACHE.get(anime);
        }

        IRestAction<byte[]> image = new FileDownloader(String.format(
                "https://media.anisekai.fr/%s.png",
                anime.getId()
        ));
        byte[] data = image.complete();
        FILE_CACHE.put(anime, data);
        return data;
    }

    @Override
    public @NotNull RequestMethod getRequestMethod() {

        return RequestMethod.GET;
    }

    @Override
    public @NotNull String getRequestURL() {

        return this.url;
    }

    /**
     * Convert the response body to the desired type for this request.
     *
     * @param response
     *         The response body received.
     *
     * @return The response converted into the requested type.
     */
    @Override
    public byte[] convert(IRestResponse response) {

        return response.getBody();
    }

}
