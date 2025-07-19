package fr.anisekai.web.packets;

import fr.alexpado.lib.rest.RestAction;
import fr.alexpado.lib.rest.enums.RequestMethod;
import fr.alexpado.lib.rest.interfaces.IRestResponse;
import fr.anisekai.web.packets.results.UserToken;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RefreshTokenPacket extends RestAction<UserToken> {

    private final UserToken userToken;

    public RefreshTokenPacket(UserToken userToken) {

        this.userToken = userToken;
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
            this.put("User-Agent", "Bravediver/JavaSpring");
            this.put("Accept-Language", "en-US,en;q=0.5");
            this.put("Content-Type", "application/x-www-form-urlencoded");
        }};
    }

    @Override
    public @NotNull String getRequestBody() {

        return "grant_type=refresh_token&refresh_token=%s".formatted(this.userToken.getRefreshToken());
    }

    @Override
    public UserToken convert(IRestResponse response) {

        byte[]     body    = response.getBody();
        String     content = new String(body);
        JSONObject json    = new JSONObject(content);
        this.userToken.load(json);
        return this.userToken;
    }

}
