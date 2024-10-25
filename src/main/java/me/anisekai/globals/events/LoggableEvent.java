package me.anisekai.globals.events;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;

/**
 * @deprecated Events should not be logged to Discord as it fills up the event queue too fast. It would be wiser to
 *         consider a web alternative.
 */
@Deprecated
public interface LoggableEvent extends DiscordEmbeddable {

    @Override
    default boolean showToEveryone() {

        return true;
    }

}
