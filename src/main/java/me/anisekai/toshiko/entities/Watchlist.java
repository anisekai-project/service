package me.anisekai.toshiko.entities;

import me.anisekai.toshiko.enums.AnimeStatus;
import net.dv8tion.jda.api.entities.Message;

import javax.persistence.*;

@Entity
public class Watchlist {

    @Id
    @Enumerated(EnumType.STRING)
    private AnimeStatus status;

    @Column(unique = true,
            nullable = false)
    private Long messageId;

    public Watchlist() {}

    public Watchlist(AnimeStatus status, Message message) {

        this.status    = status;
        this.messageId = message.getIdLong();
    }

    public AnimeStatus getStatus() {

        return this.status;
    }

    public Long getMessageId() {

        return this.messageId;
    }

    public void setMessageId(Long messageId) {

        this.messageId = messageId;
    }


}
