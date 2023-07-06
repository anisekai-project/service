package me.anisekai.toshiko.lib;

import fr.alexpado.lib.rest.RestAction;
import fr.alexpado.lib.rest.enums.RequestMethod;
import fr.alexpado.lib.rest.exceptions.RestException;
import fr.alexpado.lib.rest.interfaces.IRestAction;
import fr.alexpado.lib.rest.interfaces.IRestResponse;
import me.anisekai.toshiko.configurations.AutoDownloadConfiguration;
import me.anisekai.toshiko.data.NyaaRssItem;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class TransmissionDaemonClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransmissionDaemonClient.class);

    private final AutoDownloadConfiguration configuration;
    private       Optional<String>          sessionId = Optional.empty();

    public TransmissionDaemonClient(AutoDownloadConfiguration configuration) {

        this.configuration = configuration;
    }


    private JSONObject send(JSONObject data, boolean session) throws Exception {

        IRestAction<JSONObject> action = new RestAction<>() {

            /**
             * Retrieve the {@link RequestMethod} to use when sending the request.
             *
             * @return The {@link RequestMethod} to use.
             */
            @Override
            public @NotNull RequestMethod getRequestMethod() {

                return RequestMethod.POST;
            }

            /**
             * Retrieve the request URL to which the request should be sent.
             *
             * @return The request URL.
             */
            @Override
            public @NotNull String getRequestURL() {

                return TransmissionDaemonClient.this.configuration.getRpc();
            }

            /**
             * Retrieve a map of header that should be included in the request.
             *
             * @return A map of http headers.
             */
            @Override
            public @NotNull Map<String, String> getRequestHeaders() {

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                TransmissionDaemonClient.this.sessionId.ifPresent(id -> headers.put("X-Transmission-Session-Id", id));

                return headers;
            }

            /**
             * Retrieve the request body to send along with the request.
             *
             * @return The request body.
             */
            @Override
            public @NotNull String getRequestBody() {

                return data.toString();
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
            public JSONObject convert(IRestResponse response) {

                return new JSONObject(new String(response.getBody()));
            }
        };

        try {
            return action.complete();
        } catch (RestException e) {
            if (e.getCode() == 409 && !session) {
                this.sessionId = Optional.empty();
            } else if (e.getCode() == 409 && this.sessionId.isEmpty() && session) {
                LOGGER.info("Trying to authenticate...");
                this.sessionId = Optional.ofNullable(e.getHeaders().getOrDefault("X-Transmission-Session-Id", null));

                if (this.sessionId.isPresent()) {
                    return action.complete();
                }
            }

            throw e;
        }
    }

    public void getServerOptions() throws Exception {

        this.send(new JSONObject().put("method", "session-get"), true);
    }

    public JSONObject getTorrents() throws Exception {

        if (this.sessionId.isEmpty()) {
            this.getServerOptions();
        }

        return this.send(
                new JSONObject()
                        .put("method", "torrent-get")
                        .put("arguments", new JSONObject()
                                .put("fields", new JSONArray()
                                        .put("id")
                                        .put("name")
                                        .put("status")
                                        .put("downloadDir")
                                        .put("percentDone")
                                )
                        )
                , false);
    }

    public JSONObject startTorrent(NyaaRssItem entry) throws Exception {

        if (this.sessionId.isEmpty()) {
            this.getServerOptions();
        }

        return this.send(
                new JSONObject()
                        .put("method", "torrent-add")
                        .put("arguments", new JSONObject()
                                .put("filename", entry.getTorrent())
                        )
                , false);

    }

}
