package me.anisekai.api.transmission;

import fr.alexpado.lib.rest.RestAction;
import fr.alexpado.lib.rest.enums.RequestMethod;
import fr.alexpado.lib.rest.exceptions.RestException;
import fr.alexpado.lib.rest.interfaces.IRestAction;
import fr.alexpado.lib.rest.interfaces.IRestResponse;
import me.anisekai.api.json.BookshelfArray;
import me.anisekai.api.json.BookshelfJson;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TransmissionClient {

    public static final List<String> DEFAULT_TORRENT_FIELDS = Arrays.asList(
            "id",
            "name",
            "status",
            "downloadDir",
            "percentDone",
            "files"
    );

    private final String endpoint;
    private       String sessionId = null;

    public TransmissionClient(String endpoint) {

        this.endpoint = endpoint;
    }

    /**
     * Retrieve the RPC Endpoint for the transmission daemon server.
     *
     * @return An url
     */
    public String getEndpoint() {

        return this.endpoint;
    }

    /**
     * @return An optional session id for authenticating with transmission daemon server.
     */
    private Optional<String> getSessionId() {

        return Optional.ofNullable(this.sessionId);
    }

    /**
     * Send the provided {@link BookshelfJson} to the transmission daemon server.
     *
     * @param data
     *         {@link BookshelfJson} to send
     *
     * @return The query response
     *
     * @throws Exception
     *         Thrown if the query to the server fails.
     */
    private BookshelfJson sendPacket(BookshelfJson data) throws Exception {

        boolean isSessionCall = data.getString("method").equals("session-get");

        IRestAction<BookshelfJson> action = new RestAction<>() {

            @Override
            public @NotNull RequestMethod getRequestMethod() {

                return RequestMethod.POST;
            }

            @Override
            public @NotNull String getRequestURL() {

                return TransmissionClient.this.endpoint;
            }

            @Override
            public @NotNull Map<String, String> getRequestHeaders() {

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                TransmissionClient.this.getSessionId().ifPresent(id -> headers.put("X-Transmission-Session-Id", id));

                return headers;
            }

            @Override
            public @NotNull String getRequestBody() {

                return data.toString();
            }

            @Override
            public BookshelfJson convert(IRestResponse response) {

                return new BookshelfJson(new String(response.getBody()));
            }
        };

        try {
            return action.complete();
        } catch (RestException e) {
            if (e.getCode() == 409 && !isSessionCall) {
                this.sessionId = null;
            } else if (e.getCode() == 409 && this.getSessionId().isEmpty() && isSessionCall) {
                this.sessionId = e.getHeaders().getOrDefault("X-Transmission-Session-Id", null);

                if (this.getSessionId().isPresent()) {
                    return action.complete();
                }
            }

            throw e;
        }
    }

    /**
     * Refresh, if necessary, the session to the remote transmission daemon server.
     *
     * @throws Exception
     *         Thrown if the query to the server fails.
     */
    public void getSession() throws Exception {

        BookshelfJson packetData = new BookshelfJson();
        packetData.put("method", "session-get");

        this.sendPacket(packetData);
    }

    /**
     * Retrieve a {@link Set} of {@link TransmissionTorrent} from the remote transmission daemon server.
     *
     * @return A {@link Set} of {@link TransmissionTorrent}.
     *
     * @throws Exception
     *         Thrown if the query to the server fails.
     */
    public Set<TransmissionTorrent> queryTorrents() throws Exception {

        if (this.getSessionId().isEmpty()) {
            this.getSession();
        }

        BookshelfJson packetData = new BookshelfJson();
        packetData.put("method", "torrent-get");
        packetData.put("arguments.fields", DEFAULT_TORRENT_FIELDS);

        BookshelfJson response = this.sendPacket(packetData);
        String        status   = response.getString("result");

        if (!status.equals("success")) {
            throw new IllegalStateException("Transmission failed to query torrents: Response was " + status);
        }

        BookshelfJson            arguments  = response.readBookshelfJson("arguments");
        BookshelfArray           torrents   = arguments.readBookshelfArray("torrents");
        Set<TransmissionTorrent> torrentSet = new HashSet<>();

        torrents.forEachJson(data -> torrentSet.add(new TransmissionTorrent(data)));
        return torrentSet;
    }

    /**
     * Offer the provided {@link NyaaRssEntry} to the transmission daemon server.
     *
     * @param nyaaRssEntry
     *         The {@link NyaaRssEntry} to download.
     *
     * @return The server response.
     *
     * @throws Exception
     *         Thrown if the query to the server fails.
     * @throws IllegalStateException
     *         Thrown if the response indicate a failure or if the response was not parsable.
     */
    public TransmissionTorrent offerTorrent(NyaaRssEntry nyaaRssEntry) throws Exception {

        if (this.getSessionId().isEmpty()) {
            this.getSession();
        }

        BookshelfJson packetData = new BookshelfJson();
        packetData.put("method", "torrent-add");
        packetData.put("arguments.filename", nyaaRssEntry.getLink());

        BookshelfJson response = this.sendPacket(packetData);
        String        result   = response.getString("result");

        if (!result.equals("success")) {
            throw new IllegalStateException("Transmission client failed to start torrent");
        }

        BookshelfJson arguments = response.readBookshelfJson("arguments");
        BookshelfJson torrent;

        if (arguments.has("torrent-duplicate")) {
            torrent = arguments.readBookshelfJson("torrent-duplicate");
        } else if (arguments.has("torrent-added")) {
            torrent = arguments.readBookshelfJson("torrent-added");
        } else {
            throw new IllegalStateException("Transmission client failed to read server response.");
        }

        return new TransmissionTorrent(torrent);
    }


}
