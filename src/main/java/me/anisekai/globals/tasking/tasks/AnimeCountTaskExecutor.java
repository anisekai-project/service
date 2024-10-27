package me.anisekai.globals.tasking.tasks;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.globals.tasking.interfaces.TaskExecutor;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.linn.services.data.AnimeDataService;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class AnimeCountTaskExecutor implements TaskExecutor {

    private final AnimeDataService service;
    private final TextChannel      channel;

    public AnimeCountTaskExecutor(AnimeDataService service, TextChannel channel) {

        this.service = service;
        this.channel = channel;
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

        timer.action("load-data", "Loading task data");
        List<Anime> animes = this.service.fetchAll(repository -> repository.findAllByStatusIn(AnimeStatus.getDisplayable()));
        int         amount = animes.size();
        timer.endAction();

        timer.action("update-topic", "Update the text channel description");
        String description = String.format("Il y a en tout %s anime(s).", amount);
        this.channel.getManager().setTopic(description).complete();
        timer.endAction();
    }

}
