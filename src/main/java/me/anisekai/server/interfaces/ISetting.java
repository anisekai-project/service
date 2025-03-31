package me.anisekai.server.interfaces;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.server.events.setting.SettingValueChangedEvent;

/**
 * Interface representing an object holding data about a setting.
 */
public interface ISetting extends IEntity<String> {

    /**
     * Retrieve this {@link ISetting}'s value.
     *
     * @return A value.
     */
    String getValue();

    /**
     * Define this {@link ISetting}'s value.
     *
     * @param value
     *         A value.
     */
    @TriggerEvent(SettingValueChangedEvent.class)
    void setValue(String value);

}
