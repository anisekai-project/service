package fr.anisekai.web.data;

import fr.anisekai.web.enums.SessionType;
import fr.anisekai.web.exceptions.MalformedAuthenticationTokenException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record AuthenticationKey(@NotNull SessionType type, @NotNull String key) {

    public JSONObject toJson() {

        return new JSONObject()
                .put("type", this.type.name().toLowerCase())
                .put("key", this.key);
    }

    public String toBase64() {

        return Base64.getEncoder().encodeToString(this.toJson().toString().getBytes(StandardCharsets.UTF_8));
    }

    public static AuthenticationKey fromJson(JSONObject json) {

        SessionType type;
        String      key;

        try {
            type = SessionType.valueOf(json.getString("type").toUpperCase());
        } catch (Exception e) {
            throw new MalformedAuthenticationTokenException("Unable to parse 'type'", e);
        }

        try {
            key = json.getString("key");
            assert key != null;
        } catch (Exception e) {
            throw new MalformedAuthenticationTokenException("Unable to parse 'key'", e);
        }

        return new AuthenticationKey(type, key);
    }

    public static AuthenticationKey fromBase64(String base64) {

        JSONObject json;

        try {
            json = new JSONObject(new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new MalformedAuthenticationTokenException("Unable to parse AuthenticationKey", e);
        }

        return fromJson(json);
    }

}
