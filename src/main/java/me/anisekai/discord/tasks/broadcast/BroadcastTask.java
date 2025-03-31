package me.anisekai.discord.tasks.broadcast;

import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.plannifier.interfaces.entities.Plannifiable;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.interfaces.IAnime;
import me.anisekai.server.services.BroadcastService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.utils.FileDownloader;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public abstract class BroadcastTask implements TaskExecutor {


    public static final String           OPT_BROADCAST = "broadcast";
    private final       JDAStore         store;
    private final       BroadcastService service;

    public BroadcastTask(JDAStore store, BroadcastService service) {

        this.store   = store;
        this.service = service;
    }

    public BroadcastService getService() {

        return this.service;
    }

    public JDAStore getStore() {

        return this.store;
    }

    public Guild getGuild() throws Exception {

        return this.getStore()
                   .getGuild()
                   .orElseThrow(() -> new Exception("The Guild is not available."));
    }

    public EventData getEventData(Plannifiable<? extends IAnime<?>> plannifiable) throws Exception {

        return new EventData(plannifiable);
    }

    /**
     * Check if the executor can find the required content in the provide {@link BookshelfJson} for its execution.
     *
     * @param params
     *         A {@link BookshelfJson}
     *
     * @return True if the json contains all settings, false otherwise.
     */
    @Override
    public boolean validateParams(BookshelfJson params) {

        return params.has("broadcast");
    }

    public static class EventData {

        public final String         title;
        public final String         description;
        public final Icon           icon;
        public final OffsetDateTime startTime;
        public final OffsetDateTime endTime;

        public EventData(Plannifiable<? extends IAnime<?>> plannifiable) throws Exception {

            this.title       = "Soirée Anime";
            this.description = getEpisodeText(plannifiable);
            this.icon        = getIcon(plannifiable);
            this.startTime   = plannifiable.getStartingAt().toOffsetDateTime();
            this.endTime     = plannifiable.getEndingAt().toOffsetDateTime();
        }

        private static @NotNull Icon getIcon(Plannifiable<? extends IAnime<?>> plannifiable) throws Exception {

            FileDownloader downloader = new FileDownloader(String.format(
                    "https://media.anisekai.fr/%s.png",
                    plannifiable.getWatchTarget().getId()
            ));

            return Icon.from(downloader.complete());
        }

        private static @NotNull String getEpisodeText(Plannifiable<? extends IAnime<?>> broadcast) {

            String episodeText;
            if (broadcast.getEpisodeCount() == 1) {
                episodeText = String.format("%02d", broadcast.getFirstEpisode());
            } else if (broadcast.getEpisodeCount() == 2) {
                episodeText = String.format(
                        "%02d et %02d",
                        broadcast.getFirstEpisode(),
                        broadcast.getFirstEpisode() + 1
                );
            } else {
                episodeText = String.format(
                        "%02d à %02d",
                        broadcast.getFirstEpisode(),
                        broadcast.getFirstEpisode() + broadcast.getEpisodeCount() - 1
                );
            }
            return episodeText;
        }

    }

}
