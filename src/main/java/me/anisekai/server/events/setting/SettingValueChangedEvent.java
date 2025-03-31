package me.anisekai.server.events.setting;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.Setting;

public class SettingValueChangedEvent extends EntityUpdatedEvent<Setting, String> {

    public SettingValueChangedEvent(Object source, Setting entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
