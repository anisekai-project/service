package me.anisekai.modules.toshiko.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "anisekai.toshiko")
public class ToshikoConfiguration {

    /**
     * Define the bot token to use
     */
    private String token;

    /**
     * Define if the bot should be started.
     */
    private Boolean botEnabled;

    /**
     * The server Discord Snowflake ID.
     */
    private long serverId;

    /**
     * The channel Discord Snowflake ID into which announcement will be sent.
     */
    private long announceChannelId;

    /**
     * The role Discord Snowflake ID which will be pinged when an announcement is posted.
     */
    private long announceRoleId;

    /**
     * The channel Discord Snowflake ID into which watchlists will be sent.
     */
    private long watchlistChannelId;

    /**
     * The channel Discord Snowflake ID into which audit log will be sent.
     */
    private long auditLogChannelId;

    public String getToken() {

        return this.token;
    }

    public void setToken(String token) {

        this.token = token;
    }

    public Boolean isBotEnabled() {

        return this.botEnabled != null && this.botEnabled;
    }

    public void setBotEnabled(Boolean botEnabled) {

        this.botEnabled = botEnabled;
    }

    public long getServerId() {

        return this.serverId;
    }

    public void setServerId(long serverId) {

        this.serverId = serverId;
    }

    public long getAnnounceChannelId() {

        return this.announceChannelId;
    }

    public void setAnnounceChannelId(long announceChannelId) {

        this.announceChannelId = announceChannelId;
    }

    public long getAnnounceRoleId() {

        return this.announceRoleId;
    }

    public void setAnnounceRoleId(long announceRoleId) {

        this.announceRoleId = announceRoleId;
    }

    public long getWatchlistChannelId() {

        return this.watchlistChannelId;
    }

    public void setWatchlistChannelId(long watchlistChannelId) {

        this.watchlistChannelId = watchlistChannelId;
    }

    public long getAuditLogChannelId() {

        return this.auditLogChannelId;
    }

    public void setAuditLogChannelId(long auditLogChannelId) {

        this.auditLogChannelId = auditLogChannelId;
    }

}
