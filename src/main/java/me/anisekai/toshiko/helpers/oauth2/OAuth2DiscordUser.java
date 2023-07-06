package me.anisekai.toshiko.helpers.oauth2;

import net.dv8tion.jda.api.entities.UserSnowflake;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.*;

public class OAuth2DiscordUser implements UserSnowflake, OAuth2User, Serializable {

    private final Collection<? extends GrantedAuthority> authorities;
    private final String                                 id;
    private final String                                 username;
    private final String                                 avatarHash;

    public OAuth2DiscordUser(Collection<GrantedAuthority> authorities, Map<String, Object> attributes) {

        this.authorities = (authorities != null)
                ? Collections.unmodifiableSet(new LinkedHashSet<>(this.sortAuthorities(authorities)))
                : Collections.unmodifiableSet(new LinkedHashSet<>(AuthorityUtils.NO_AUTHORITIES));

        if (attributes != null) {
            JSONObject source = new JSONObject(attributes);
            this.id         = source.getString("id");
            this.username   = source.getString("username");
            this.avatarHash = source.optString("avatar");
        } else {
            this.id         = null;
            this.username   = null;
            this.avatarHash = null;
        }
    }

    private Set<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {

        Set<GrantedAuthority> sortedAuthorities = new TreeSet<>(
                Comparator.comparing(GrantedAuthority::getAuthority));
        sortedAuthorities.addAll(authorities);
        return sortedAuthorities;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.authorities;
    }

    @Override
    public String getName() {

        return this.username;
    }

    @Override
    public @NotNull String getDefaultAvatarId() {

        return String.valueOf((this.getIdLong() >> 22) % 5);
    }

    @Override
    public @NotNull String getAsMention() {

        return UserSnowflake.fromId(this.getIdLong()).getAsMention();
    }

    @Override
    public long getIdLong() {

        return Long.parseLong(this.id);
    }

    @Override
    public @NotNull String getId() {

        return this.id;
    }

    public String getUsername() {

        return this.username;
    }

    public String getAvatarHash() {

        return this.avatarHash == null ? this.getDefaultAvatarId() : this.avatarHash;
    }

}
