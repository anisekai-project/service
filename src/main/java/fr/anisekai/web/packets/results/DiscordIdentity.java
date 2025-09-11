package fr.anisekai.web.packets.results;

import org.json.JSONObject;

public class DiscordIdentity {

    private final long   id;
    private final String username;
    private final String discriminator;
    private final String globalName;
    private final String avatar;

    public DiscordIdentity(JSONObject json) {

        this.id            = Long.parseLong(json.getString("id"));
        this.username      = json.getString("username");
        this.discriminator = json.getString("discriminator");
        this.globalName    = json.getString("global_name");
        this.avatar        = json.getString("avatar");
    }

    public long getId() {

        return this.id;
    }

    public String getUsername() {

        return this.username;
    }

    public String getDiscriminator() {

        return this.discriminator;
    }

    public String getGlobalName() {

        return this.globalName;
    }

    public String getAvatar() {

        return this.avatar;
    }

    @Override
    public String toString() {

        return "DiscordIdentity{id=%d, username='%s', discriminator='%s', globalName='%s', avatar='%s'}".formatted(
                this.id,
                this.username,
                this.discriminator,
                this.globalName,
                this.avatar
        );
    }

}
