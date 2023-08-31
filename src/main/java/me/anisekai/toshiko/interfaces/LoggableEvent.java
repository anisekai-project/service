package me.anisekai.toshiko.interfaces;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

public interface LoggableEvent extends DiscordEmbeddable {

    @Override
    default boolean showToEveryone() {
        return true;
    }

}
