package me.anisekai.modules.shizue.interfaces.entities;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.entities.keys.SeasonalVoterKey;

public interface ISeasonalVoter extends IEntity<SeasonalVoterKey> {

    SeasonalSelection getSeasonalSelection();

    void setSeasonalSelection(SeasonalSelection seasonalSelection);

    DiscordUser getUser();

    void setUser(DiscordUser user);

    int getAmount();

    void setAmount(int amount);

}
