package me.anisekai.toshiko.events;

import me.anisekai.toshiko.entities.AnimeNight;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.context.ApplicationEvent;

public class AnimeNightUpdateEvent extends ApplicationEvent {

    private final Guild      guild;
    private final AnimeNight animeNight;

    public AnimeNightUpdateEvent(Object source, Guild guild, AnimeNight animeNight) {

        super(source);
        this.guild        = guild;
        this.animeNight   = animeNight;
    }

    public Guild getGuild() {

        return this.guild;
    }

    public AnimeNight getAnimeNight() {

        return this.animeNight;
    }
}
