package me.anisekai.toshiko.interfaces.entities;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.interfaces.persistence.IEntity;

import java.util.Set;

public interface IWatchlist extends IEntity<AnimeStatus>, Comparable<IWatchlist> {

    Long getMessageId();

    void setMessageId(Long messageId);

    Set<Anime> getAnimes();

    void setAnimes(Set<Anime> animes);

    int getOrder();

    void setOrder(int order);

}
