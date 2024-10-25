package me.anisekai.modules.chiya.interfaces;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.modules.chiya.events.user.*;

public interface IUser extends IEntity<Long> {

    String getUsername();

    @TriggerEvent(UserUsernameUpdatedEvent.class)
    void setUsername(String username);

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
