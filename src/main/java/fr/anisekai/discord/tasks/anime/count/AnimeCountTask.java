package fr.anisekai.discord.tasks.anime.count;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.anisekai.discord.JDAStore;
import fr.anisekai.discord.exceptions.tasks.UndefinedWatchlistChannelException;
import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.services.AnimeService;
import fr.anisekai.server.tasking.TaskExecutor;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.remote.enums.AnimeList;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class AnimeCountTask implements TaskExecutor {

    private final AnimeService service;
    private final JDAStore     store;

    public AnimeCountTask(AnimeService service, JDAStore store) {

        this.service = service;
        this.store   = store;
    }

    @Override
    public void execute(ITimedAction timer, AnisekaiJson params) {

        timer.action("load", "Loading task data");
        List<Anime> animes  = this.service.fetchAll(repository -> repository.findAllByListIn(AnimeList.collect(AnimeList.Property.SHOW)));
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
