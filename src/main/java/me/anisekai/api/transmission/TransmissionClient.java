package me.anisekai.api.transmission;

import fr.alexpado.lib.rest.RestAction;
import fr.alexpado.lib.rest.enums.RequestMethod;
import fr.alexpado.lib.rest.exceptions.RestException;
import fr.alexpado.lib.rest.interfaces.IRestAction;
import fr.alexpado.lib.rest.interfaces.IRestResponse;
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

    private Optional<String> getSessionId() {

        return Optional.ofNullable(this.sessionId);
    }

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

    public BookshelfJson getSession() throws Exception {

        BookshelfJson packetData = new BookshelfJson();
        packetData.put("method", "session-get");

        return this.sendPacket(packetData);
    }

    public BookshelfJson getTorrents() throws Exception {

        if (this.getSessionId().isEmpty()) {
            this.getSession();
        }

        BookshelfJson packetData = new BookshelfJson();
        packetData.put("method", "torrent-get");
        packetData.put("arguments.fields", DEFAULT_TORRENT_FIELDS);

        return this.sendPacket(packetData);
    }

    public BookshelfJson startTorrent(String source) throws Exception {

        if (this.getSessionId().isEmpty()) {
            this.getSession();
        }

        BookshelfJson packetData = new BookshelfJson();
        packetData.put("method", "torrent-add");
        packetData.put("arguments.filename", source);

        return this.sendPacket(packetData);
    }


}
