package me.anisekai.server.events;

import me.anisekai.server.entities.Setting;

/**
 * Event notifying when a {@link Setting} is being inserted in the database.
 */
public class SettingCreatedEvent extends EntityCreatedEventAdapter<Setting> {

    public SettingCreatedEvent(Object source, Setting entity) {

        super(source, entity);
    }

}
