package me.anisekai.toshiko.interfaces.entities;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.entities.keys.SeasonalVoterKey;
import me.anisekai.toshiko.interfaces.persistence.IEntity;

public interface ISeasonalVoter extends IEntity<SeasonalVoterKey> {

    SeasonalSelection getSeasonalSelection();

    void setSeasonalSelection(SeasonalSelection seasonalSelection);

    DiscordUser getUser();

    void setUser(DiscordUser user);

    int getAmount();

    void setAmount(int amount);

}
