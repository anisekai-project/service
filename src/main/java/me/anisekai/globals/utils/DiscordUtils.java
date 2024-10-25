package me.anisekai.globals.utils;

import me.anisekai.modules.linn.interfaces.IAnime;
import me.anisekai.modules.shizue.entities.SeasonalSelection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.springframework.context.ApplicationEvent;

import java.time.OffsetDateTime;

/**
 * Utility class containing various method for Discord formatting
 */
public final class DiscordUtils {

    private DiscordUtils() {}

    /**
     * Creates a Markdown link with the provided name and link.
     *
     * @param name
     *         The display name for the Markdown link
     * @param link
     *         The target for the Markdown link
     *
     * @return A Markdown link as string.
     */
    public static String link(String name, String link) {

        return "[%s](%s)".formatted(name, link);
    }

    /**
     * Creates a Markdown link for the {@link IAnime} provided.
     *
     * @param anime
     *         The {@link IAnime} for which the link will be created.
     *
     * @return A Markdown link as string.
     */
    public static String link(IAnime anime) {

        return link(anime.getName(), anime.getLink());
    }

    /**
     * Creates a basic {@link EmbedBuilder} based on the {@link ApplicationEvent} provided.
     *
     * @param event
     *         The {@link ApplicationEvent}
     *
     * @return An {@link EmbedBuilder} with a footer and a timestamp already defined using the {@link ApplicationEvent}
     *         provided.
     */
    public static EmbedBuilder event(ApplicationEvent event) {

        return new EmbedBuilder()
                .setFooter(event.getSource().getClass().getName())
                .setTimestamp(OffsetDateTime.now());
    }

    /**
     * Create a command choice with the provided id and name. If the name is longer than 100 characters, the string will
     * be cut and '...' will be appended.
     *
     * @param id
     *         The choice's id
     * @param name
     *         The choice's name
     *
     * @return A command choice
     */
    public static Command.Choice asChoice(long id, String name) {

        if (name.length() > 100) {
            return new Command.Choice(String.format("%s...", name.substring(0, 90)), id);
        }
        return new Command.Choice(name, id);
    }

    /**
     * Transform the provided {@link SeasonalSelection} into a command choice.
     *
     * @param selection
     *         The {@link SeasonalSelection} to transform
     *
     * @return A command choice
     */
    public static Command.Choice asChoice(SeasonalSelection selection) {

        return asChoice(selection.getId(), selection.getName());
    }

}
