package fr.anisekai.discord;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "discord")
public class DiscordConfiguration {

    private String  clientId;
    private String  clientSecret;
    private String  scope;
    private String  redirectUri;
    private String  botToken;
    private boolean botEnabled;

    public String getClientId() {

        return this.clientId;
    }

    public void setClientId(String clientId) {

        this.clientId = clientId;
    }

    public String getClientSecret() {

        return this.clientSecret;
    }

    public void setClientSecret(String clientSecret) {

        this.clientSecret = clientSecret;
    }

    public String getScope() {

        return this.scope;
    }

    public void setScope(String scope) {

        this.scope = scope;
    }

    public String getRedirectUri() {

        return this.redirectUri;
    }

    public void setRedirectUri(String redirectUri) {

        this.redirectUri = redirectUri;
    }

    public String getBotToken() {

        return this.botToken;
    }

    public void setBotToken(String token) {

        this.botToken = token;
    }

    public boolean isBotEnabled() {

        return this.botEnabled;
    }

    public void setBotEnabled(boolean enabled) {

        this.botEnabled = enabled;
    }

}
