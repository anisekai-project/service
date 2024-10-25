package me.anisekai.modules.shizue.interfaces.entities;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.entities.keys.UserAnimeVoteAssocKey;

public interface ISeasonalVote extends IEntity<UserAnimeVoteAssocKey> {

    SeasonalSelection getSeasonalSelection();

    void setSeasonalSelection(SeasonalSelection seasonalSelection);

    DiscordUser getUser();

    void setUser(DiscordUser user);

    Anime getAnime();

    void setAnime(Anime anime);

}
