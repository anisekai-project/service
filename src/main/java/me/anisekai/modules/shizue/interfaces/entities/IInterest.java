package me.anisekai.modules.shizue.interfaces.entities;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.entities.keys.UserAnimeAssocKey;
import me.anisekai.modules.shizue.enums.InterestLevel;
import me.anisekai.modules.shizue.events.interest.InterestLevelUpdatedEvent;

public interface IInterest extends IEntity<UserAnimeAssocKey> {

    Anime getAnime();

    void setAnime(Anime anime);

    DiscordUser getUser();

    void setUser(DiscordUser user);

    InterestLevel getLevel();

    @TriggerEvent(InterestLevelUpdatedEvent.class)
    void setLevel(InterestLevel level);

}
