package fr.anisekai.web.data;

import fr.anisekai.web.exceptions.MalformedAuthenticationTokenException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public record SessionToken(AuthenticationKey auth, UUID uuid) {

    public JSONObject toJson() {

        return new JSONObject()
                .put("auth", this.auth.toJson())
                .put("uuid", this.uuid.toString());
    }

    public String toBase64() {

        return Base64.getEncoder().encodeToString(this.toJson().toString().getBytes(StandardCharsets.UTF_8));
    }

    public static SessionToken fromJson(JSONObject json) {

        AuthenticationKey auth;
        UUID              uuid;

        try {
            auth = AuthenticationKey.fromJson(json.getJSONObject("auth"));
        } catch (Exception e) {
            throw new MalformedAuthenticationTokenException("Unable to parse 'auth'", e);
        }

        try {
            uuid = UUID.fromString(json.getString("uuid"));
        } catch (Exception e) {
            throw new MalformedAuthenticationTokenException("Unable to parse 'uuid'", e);
        }

        return new SessionToken(auth, uuid);
    }

    public static SessionToken fromBase64(String base64) {

        JSONObject json;

        try {
            json = new JSONObject(new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new MalformedAuthenticationTokenException("Unable to parse AuthenticationKey", e);
        }

        return fromJson(json);
    }

}
