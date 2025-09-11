package fr.anisekai.web.packets;

import fr.alexpado.lib.rest.RestAction;
import fr.alexpado.lib.rest.enums.RequestMethod;
import fr.alexpado.lib.rest.interfaces.IRestResponse;
import fr.anisekai.ApplicationConfiguration;
import fr.anisekai.BuildInfo;
import fr.anisekai.web.packets.results.UserToken;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AuthTokenPacket extends RestAction<UserToken> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenPacket.class);

    private final ApplicationConfiguration.Discord.OAuth configuration;
    private final String                                 code;

    public AuthTokenPacket(ApplicationConfiguration.Discord.OAuth configuration, String code) {

        this.configuration = configuration;
        this.code          = code;
    }

    @Override
    public @NotNull RequestMethod getRequestMethod() {

        return RequestMethod.POST;
    }

    @Override
    public @NotNull String getRequestURL() {

        return "https://discordapp.com/api/oauth2/token";
    }

    @Override
    public @NotNull Map<String, String> getRequestHeaders() {

        return new HashMap<>() {{
            this.put("User-Agent", "anisekai/service " + BuildInfo.getVersion());
            this.put("Accept-Language", "en-US,en;q=0.5");
            this.put("Content-Type", "application/x-www-form-urlencoded");
        }};
    }

    @Override
    public @NotNull String getRequestBody() {

        return "client_id=%s&client_secret=%s&grant_type=authorization_code&code=%s&redirect_uri=%s&scope=%s".formatted(
                this.configuration.getClientId(),
                this.configuration.getClientSecret(),
                this.code,
                URLEncoder.encode(this.configuration.getRedirectUri(), StandardCharsets.UTF_8),
                this.configuration.getScope()
        );
    }

    @Override
    public UserToken convert(IRestResponse response) {

        byte[]     body    = response.getBody();
        String     content = new String(body);
        JSONObject json    = new JSONObject(content);
        return new UserToken(json);
    }

}
