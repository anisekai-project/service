package fr.anisekai.utils;

import fr.alexpado.lib.rest.RestAction;
import fr.alexpado.lib.rest.enums.RequestMethod;
import fr.alexpado.lib.rest.exceptions.RestException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileUrlStreamer extends RestAction<Boolean> {

    private final Path   file;
    private final String url;

    public FileUrlStreamer(Path file, String url) {

        this.file = file;
        this.url  = url;

        if (Files.isDirectory(file)) {
            throw new IllegalArgumentException(file + " is a directory");
        }
    }

    @Override
    public @NotNull RequestMethod getRequestMethod() {

        return RequestMethod.GET;
    }

    @Override
    public @NotNull String getRequestURL() {

        return this.url;
    }

    @Override
    public Boolean complete() throws Exception {

        String urlStr;
        // Build the URL.
        if (this.getRequestMethod() == RequestMethod.GET && !this.getRequestParameters().isEmpty()) {
            urlStr = String.format("%s?%s", this.getRequestURL(), mergeMapAsQuery(this.getRequestParameters()));
        } else {
            urlStr = this.getRequestURL();
        }

        URLConnection     connection = URI.create(urlStr).toURL().openConnection();
        HttpURLConnection http       = ((HttpURLConnection) connection);

        http.setRequestMethod(this.getRequestMethod().name());
        this.getRequestHeaders().forEach(http::setRequestProperty);

        String requestBody = this.getRequestBody();

        if (this.getRequestMethod().isOutputSupported() && !requestBody.isEmpty()) {
            http.setDoOutput(true);

            try (OutputStream os = http.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int     httpCode = http.getResponseCode();
        boolean isOk     = httpCode >= HttpURLConnection.HTTP_OK && httpCode < HttpURLConnection.HTTP_MULT_CHOICE;

        if (httpCode == HttpURLConnection.HTTP_NO_CONTENT) {
            return false;
        }

        Map<String, String> headers = new HashMap<>();
        http.getHeaderFields().forEach((key, values) -> {
            if (key != null && values != null) {
                headers.put(key, String.join(", ", values));
            }
        });

        if (isOk) {
            try (InputStream is = http.getInputStream()) {
                try (OutputStream os = new FileOutputStream(this.file.toFile())) {
                    is.transferTo(os);
                }
            }
            http.disconnect();
            return true;
        }

        byte[] responseBody = new byte[0];

        InputStream err = http.getErrorStream();
        if (err != null) {
            try (err) {
                responseBody = err.readAllBytes();
            }
        }

        http.disconnect();
        throw new RestException(responseBody, httpCode, headers);
    }

}
