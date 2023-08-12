package me.anisekai.toshiko.interfaces.entities;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.entities.keys.UserAnimeVoteAssocKey;
import me.anisekai.toshiko.interfaces.persistence.IEntity;

public interface ISeasonalVote extends IEntity<UserAnimeVoteAssocKey> {

    SeasonalSelection getSeasonalSelection();

    void setSeasonalSelection(SeasonalSelection seasonalSelection);

    DiscordUser getUser();

    void setUser(DiscordUser user);

    Anime getAnime();

    void setAnime(Anime anime);

}
