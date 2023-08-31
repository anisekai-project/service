package me.anisekai.toshiko.interfaces.entities;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.TorrentStatus;
import me.anisekai.toshiko.events.torrent.TorrentPercentDoneUpdatedEvent;
import me.anisekai.toshiko.events.torrent.TorrentStatusUpdatedEvent;
import me.anisekai.toshiko.interfaces.persistence.IEntity;
import me.anisekai.toshiko.helpers.proxy.TriggerEvent;

public interface ITorrent extends IEntity<Integer> {

    Anime getAnime();

    void setAnime(Anime anime);

    String getLink();

    void setLink(String link);

    String getName();

    void setName(String name);

    TorrentStatus getStatus();

    @TriggerEvent(TorrentStatusUpdatedEvent.class)
    void setStatus(TorrentStatus status);

    String getDownloadDir();

    void setDownloadDir(String downloadDir);

    double getPercentDone();

    @TriggerEvent(TorrentPercentDoneUpdatedEvent.class)
    void setPercentDone(double percentDone);

    String getInfoHash();

    void setInfoHash(String infoHash);

    String getFile();

    void setFile(String file);

}
