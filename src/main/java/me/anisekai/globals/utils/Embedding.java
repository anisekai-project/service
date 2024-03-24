package me.anisekai.globals.utils;

import me.anisekai.modules.linn.interfaces.IAnime;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.context.ApplicationEvent;

import java.time.OffsetDateTime;

public final class Embedding {

    private Embedding() {}

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

}
