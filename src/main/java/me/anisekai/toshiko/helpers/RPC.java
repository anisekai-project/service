package me.anisekai.toshiko.helpers;

import fr.alexpado.lib.rest.RestAction;
import fr.alexpado.lib.rest.enums.RequestMethod;
import fr.alexpado.lib.rest.exceptions.RestException;
import fr.alexpado.lib.rest.interfaces.IRestAction;
import fr.alexpado.lib.rest.interfaces.IRestResponse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RPC {

    private final static Logger LOGGER = LoggerFactory.getLogger(RPC.class);

    @Value("${toshiko.rpc.url:#{null}}")
    private String target;

    private Optional<String>     sessionId     = Optional.empty();
    private Optional<JSONObject> optionalCache = Optional.empty();

    public boolean isReady() {
        return this.target != null;
    }

    private JSONObject send(JSONObject data, boolean session) throws Exception {

        if (this.target == null) {
            throw new IllegalStateException("RPC service not ready.");
        }

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

                return RPC.this.target;
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
                RPC.this.sessionId.ifPresent(id -> headers.put("X-Transmission-Session-Id", id));

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

            /**
             * Send the http request synchronously, returning the http response, or throwing an
             * exception if an error occurs.
             *
             * @return The http response.
             *
             * @throws RestException
             *         If the http response code is not in the 2xx range.
             * @throws Exception
             *         If the http couldn't be sent or if the response couldn't be parsed.
             */
            @Override
            public JSONObject complete() throws Exception {

                LOGGER.debug("Sending packet: {}", data.toString());
                return super.complete();
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
                                        .put("addedDate")
                                        .put("name")
                                        .put("error")
                                        .put("errorString")
                                        .put("eta")
                                        .put("isFinished")
                                        .put("isStalled")
                                        .put("leftUntilDone")
                                        .put("metadataPercentComplete")
                                        .put("peersConnected")
                                        .put("peersGettingFromUs")
                                        .put("peersSendingToUs")
                                        .put("percentDone")
                                        .put("queuePosition")
                                        .put("rateDownload")
                                        .put("rateUpload")
                                        .put("recheckProgress")
                                        .put("seedRatioMode")
                                        .put("seedRatioLimit")
                                        .put("sizeWhenDone")
                                        .put("status")
                                        .put("trackers")
                                        .put("downloadDir")
                                        .put("uploadedEver")
                                        .put("uploadRatio")
                                )
                        )
                , false);
    }

    public void putCache(JSONObject o) {

        this.optionalCache = Optional.ofNullable(o);
    }

    public Optional<JSONObject> getCache() {

        return this.optionalCache;
    }

}
