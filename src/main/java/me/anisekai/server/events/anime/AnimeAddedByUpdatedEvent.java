package me.anisekai.server.events.anime;

import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.AnimeUpdatedEvent;

public class AnimeAddedByUpdatedEvent extends AnimeUpdatedEvent<DiscordUser> {

    public AnimeAddedByUpdatedEvent(Object source, Anime entity, DiscordUser previous, DiscordUser current) {

        super(source, entity, previous, current);
    }

}
