package me.anisekai.discord.tasks.anime.count;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.discord.JDAStore;
import me.anisekai.discord.exceptions.tasks.UndefinedWatchlistChannelException;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.enums.AnimeStatus;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.tasking.TaskExecutor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class AnimeCountTask implements TaskExecutor {

    private final AnimeService service;
    private final JDAStore     store;

    public AnimeCountTask(AnimeService service, JDAStore store) {

        this.service = service;
        this.store   = store;
    }

    /**
     * Run this task.
     *
     * @param timer
     *         The timer to use to mesure performance of the task.
     * @param params
     *         The parameters of this task.
     */
    @Override
    public void execute(ITimedAction timer, BookshelfJson params) {

        timer.action("load", "Loading task data");
        List<Anime> animes  = this.service.fetchAll(repository -> repository.findAllByStatusIn(AnimeStatus.getDisplayable()));
        int         amount  = animes.size();
        TextChannel channel = this.getWatchlistChannel();
        timer.endAction();

        timer.action("update-topic", "Update the text channel description");
        String description = String.format("Il y a en tout %s anime(s).", amount);
        channel.getManager().setTopic(description).complete();
        timer.endAction();
    }

    private TextChannel getWatchlistChannel() {

        return this.store.getAnnouncementChannel().orElseThrow(UndefinedWatchlistChannelException::new);
    }

}
