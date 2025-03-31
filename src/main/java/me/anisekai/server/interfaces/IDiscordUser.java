package me.anisekai.server.interfaces;

import jakarta.annotation.Nullable;
import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.server.events.discorduser.*;

/**
 * Interface representing an object holding data about a discord user.
 */
public interface IDiscordUser extends IEntity<Long> {

    /**
     * Retrieve this {@link IDiscordUser}'s username.
     *
     * @return A username.
     */
    String getUsername();

    /**
     * Define this {@link IDiscordUser}'s username.
     *
     * @param username
     *         A username.
     */
    @TriggerEvent(DiscordUserUsernameUpdatedEvent.class)
    void setUsername(String username);

    /**
     * Retrieve the emote used by this {@link IDiscordUser} for votes.
     *
     * @return An emote.
     */
    @Nullable
    String getEmote();

    /**
     * Define the emote used by this {@link IDiscordUser} for votes.
     *
     * @param emote
     *         An emote.
     */
    @TriggerEvent(DiscordUserEmoteUpdatedEvent.class)
    void setEmote(@Nullable String emote);

    /**
     * Check if this {@link IDiscordUser} is active. An active user will have their votes counted toward the anime
     * leaderboard but will also take part in {@link ISelection} in the server.
     *
     * @return True if the user is active, false otherwise.
     */
    boolean isActive();

    /**
     * Check if this {@link IDiscordUser} is active. An active user will have their votes counted toward the anime
     * leaderboard but will also take part in {@link ISelection} in the server.
     *
     * @param active
     *         True if the user is active, false otherwise.
     */
    @TriggerEvent(DiscordUserActiveUpdatedEvent.class)
    void setActive(boolean active);

    /**
     * Check if this {@link IDiscordUser} is able to perform administrative actions on the application, regardless of
     * their permission on the discord server.
     *
     * @return True if the user has administrator rights, false otherwise.
     */
    boolean isAdministrator();

    /**
     * Define if this {@link IDiscordUser} is able to perform administrative actions on the application, regardless of
     * their permissions on the discord server.
     *
     * @param administrator
     *         True if the user has administrator rights, false otherwise.
     */
    @TriggerEvent(DiscordUserAdministratorUpdatedEvent.class)
    void setAdministrator(boolean administrator);

    /**
     * Check if this {@link IDiscordUser} is allowed to access the website content, regardless of their permissions on
     * the discord server.
     *
     * @return True if the user has access to the website, false otherwise.
     */
    boolean hasWebsiteAccess();

    /**
     * Define if this {@link IDiscordUser} is allowed to access the website content, regardless of their permissions on
     * the discord server.
     *
     * @param websiteAccess
     *         True if the user has access to the website, false otherwise.
     */
    @TriggerEvent(DiscordUserWebsiteAccessUpdatedEvent.class)
    void setWebsiteAccess(boolean websiteAccess);

    /**
     * Retrieve this {@link IDiscordUser}'s application key.
     *
     * @return An application key.
     */
    String getKey();

    /**
     * Define this {@link IDiscordUser}'s application key.
     *
     * @param key
     *         An application key.
     */
    @TriggerEvent(DiscordUserKeyUpdatedEvent.class)
    void setKey(String key);

}
