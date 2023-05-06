package me.anisekai.toshiko.tasks;

import me.anisekai.toshiko.data.Task;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.services.AnimeService;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class AnimeCountTask implements Task {

    private final AnimeService service;
    private final TextChannel  channel;

    public AnimeCountTask(AnimeService service, TextChannel channel) {

        this.service = service;
        this.channel = channel;
    }

    @Override
    public String getName() {

        return "ANIME:COUNT";
    }

    @Override
    public void run() throws Exception {

        List<Anime> animes = this.service.getRepository().findAllByStatusIn(AnimeStatus.getDisplayable());

        String description = String.format("Il y a en tout %s anime(s).", animes.size());
        this.channel.getManager().setTopic(description).complete();
    }

    @Override
    public void onFinished() {

    }

    @Override
    public void onException(Exception e) {

    }
}
