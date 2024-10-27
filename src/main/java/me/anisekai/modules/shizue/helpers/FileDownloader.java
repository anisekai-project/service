package me.anisekai.modules.shizue.helpers;

import fr.alexpado.lib.rest.RestAction;
import fr.alexpado.lib.rest.enums.RequestMethod;
import fr.alexpado.lib.rest.interfaces.IRestResponse;
import org.jetbrains.annotations.NotNull;

public class FileDownloader extends RestAction<byte[]> {

    private final String url;

    public FileDownloader(String url) {

        this.url = url;
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
