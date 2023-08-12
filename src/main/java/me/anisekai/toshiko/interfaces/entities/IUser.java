package me.anisekai.toshiko.interfaces.entities;

import me.anisekai.toshiko.events.user.*;
import me.anisekai.toshiko.interfaces.persistence.IEntity;
import me.anisekai.toshiko.helpers.proxy.TriggerEvent;

public interface IUser extends IEntity<Long> {

    String getUsername();

    @TriggerEvent(UserUsernameUpdatedEvent.class)
    void setUsername(String username);

    @Deprecated
    String getDiscriminator();

    @Deprecated
    @TriggerEvent(UserDiscriminatorUpdatedEvent.class)
    void setDiscriminator(String discriminator);

    String getEmote();

    @TriggerEvent(UserEmoteUpdatedEvent.class)
    void setEmote(String emote);

    boolean isActive();

    @TriggerEvent(UserActiveUpdatedEvent.class)
    void setActive(boolean active);

    boolean isAdmin();

    @TriggerEvent(UserAdminUpdatedEvent.class)
    void setAdmin(boolean admin);

    boolean hasWebAccess();

    @TriggerEvent(UserWebAccessUpdatedEvent.class)
    void setWebAccess(boolean webAccess);

}
