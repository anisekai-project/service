package me.anisekai.modules.shizue.interfaces.entities;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.enums.AnimeStatus;

import java.util.Set;

public interface IWatchlist extends IEntity<AnimeStatus>, Comparable<IWatchlist> {

    Long getMessageId();

    void setMessageId(Long messageId);

    Set<Anime> getAnimes();

    void setAnimes(Set<Anime> animes);

    int getOrder();

    void setOrder(int order);

}
