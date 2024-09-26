package me.anisekai.modules.shizue.utils;

import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.modules.shizue.interfaces.AnimeNightMeta;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.util.function.Consumer;

public final class BroadcastUtils {

    private BroadcastUtils() {}


    public static String asEpisodeDescription(AnimeNightMeta meta) {

        return asEpisodeDescription(meta, true);
    }

    public static String asEpisodeDescription(AnimeNightMeta meta, boolean emphasize) {

        long amount = meta.getEpisodeCount();

        if (amount == 1) {
            String prefix = emphasize ? "**Épisode**" : "épisode";
            return String.format("%s %02d", prefix, meta.getFirstEpisode());
        } else if (amount == 2) {
            String prefix = emphasize ? "**Épisodes**" : "épisodes";
            return String.format("%s %02d et %02d", prefix, meta.getFirstEpisode(), meta.getLastEpisode());
        } else {
            String prefix = emphasize ? "**Épisodes**" : "épisodes";
            return String.format("%s %02d à %02d", prefix, meta.getFirstEpisode(), meta.getLastEpisode());
        }
    }

    public static SlashResponse toSlashResponse(AnimeNightMeta meta) {

        return new SlashResponse() {
            @Override
            public Consumer<MessageRequest<?>> getHandler() {

                return (mr) -> {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle(meta.getWatchTarget().getName(), meta.getWatchTarget().getLink());
                    builder.setDescription("La séance a bien été programmée.");
                    builder.addField("Épisode(s)", asEpisodeDescription(meta), true);

                    mr.setEmbeds(builder.build());
                };
            }

            @Override
            public boolean isEphemeral() {

                return false;
            }
        };
    }

}
