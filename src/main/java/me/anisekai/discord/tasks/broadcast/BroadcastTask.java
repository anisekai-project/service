package me.anisekai.discord.tasks.broadcast;

import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.api.persistence.interfaces.Entity;
import fr.anisekai.wireless.api.plannifier.interfaces.entities.Planifiable;
import fr.anisekai.wireless.remote.interfaces.AnimeEntity;
import fr.anisekai.wireless.utils.FileDownloader;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.services.BroadcastService;
import me.anisekai.server.tasking.TaskExecutor;
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

    public EventData getEventData(Planifiable<? extends AnimeEntity<?>> planifiable) throws Exception {

        return new EventData(planifiable);
    }

    @Override
    public boolean validateParams(AnisekaiJson params) {

        return params.has("broadcast");
    }

    public static class EventData {

        public final String         title;
        public final String         description;
        public final Icon           icon;
        public final OffsetDateTime startTime;
        public final OffsetDateTime endTime;

        public EventData(Planifiable<? extends AnimeEntity<?>> planifiable) throws Exception {

            this.title       = "Soirée Anime";
            this.description = String.format(
                    "**%s**\nÉpisode(s): %s",
                    planifiable.getWatchTarget().getTitle(),
                    getEpisodeText(planifiable)
            );
            this.icon        = getIcon(planifiable);
            this.startTime   = planifiable.getStartingAt().toOffsetDateTime();
            this.endTime     = planifiable.getEndingAt().toOffsetDateTime();
        }

        private static @NotNull Icon getIcon(Planifiable<? extends Entity<?>> planifiable) throws Exception {

            FileDownloader downloader = new FileDownloader(String.format(
                    "https://media.anisekai.fr/%s.png",
                    planifiable.getWatchTarget().getId()
            ));

            return Icon.from(downloader.complete());
        }

        private static @NotNull String getEpisodeText(Planifiable<?> broadcast) {

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
