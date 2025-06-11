package fr.anisekai.discord;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "discord.bot")
public class DiscordConfiguration {

    private String  token;
    private boolean enabled;

    public String getToken() {

        return this.token;
    }

    public void setToken(String token) {

        this.token = token;
    }

    public boolean isEnabled() {

        return this.enabled;
    }

    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

}
