package me.anisekai.toshiko.interfaces.entities;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.keys.UserAnimeAssocKey;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.events.interest.InterestLevelUpdatedEvent;
import me.anisekai.toshiko.interfaces.persistence.IEntity;
import me.anisekai.toshiko.helpers.proxy.TriggerEvent;

public interface IInterest extends IEntity<UserAnimeAssocKey> {

    Anime getAnime();

    void setAnime(Anime anime);

    DiscordUser getUser();

    void setUser(DiscordUser user);

    InterestLevel getLevel();

    @TriggerEvent(InterestLevelUpdatedEvent.class)
    void setLevel(InterestLevel level);

}
