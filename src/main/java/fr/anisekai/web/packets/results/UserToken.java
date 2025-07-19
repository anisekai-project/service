package fr.anisekai.web.packets.results;

import org.json.JSONObject;

public class UserToken {

    private String accessToken;
    private String tokenType;
    private int    expiresIn;
    private String refreshToken;
    private String scope;
    private long   generationTime;
    private long   lastActivity;

    public UserToken(JSONObject json) {

        this.load(json);
        this.signalActivity();
    }

    public void load(JSONObject json) {

        this.accessToken    = json.getString("access_token");
        this.tokenType      = json.getString("token_type");
        this.expiresIn      = json.getInt("expires_in");
        this.refreshToken   = json.getString("refresh_token");
        this.scope          = json.getString("scope");
        this.generationTime = System.currentTimeMillis() / 1000;
    }

    public String getAccessToken() {

        return this.accessToken;
    }

    public String getTokenType() {

        return this.tokenType;
    }

    public int getExpiresIn() {

        return this.expiresIn;
    }

    public String getRefreshToken() {

        return this.refreshToken;
    }

    public String getScope() {

        return this.scope;
    }

    public long getGenerationTime() {

        return this.generationTime;
    }

    public long getLastActivity() {

        return this.lastActivity;
    }

    public void signalActivity() {

        this.lastActivity = System.currentTimeMillis() / 1000;
    }

    public boolean isExpired() {

        return System.currentTimeMillis() / 1000 > this.generationTime + this.expiresIn;
    }

    public boolean willExpire(long shift) {

        return System.currentTimeMillis() / 1000 > (this.generationTime + this.expiresIn - shift);
    }

    public boolean isInactive(long ttl) {

        return System.currentTimeMillis() / 1000 > this.lastActivity + ttl;
    }

    @Override
    public String toString() {

        return "UserToken{accessToken='%s', tokenType='%s', expiresIn=%d, refreshToken='%s', scope='%s'}".formatted(
                this.accessToken,
                this.tokenType,
                this.expiresIn,
                this.refreshToken,
                this.scope
        );
    }

}
