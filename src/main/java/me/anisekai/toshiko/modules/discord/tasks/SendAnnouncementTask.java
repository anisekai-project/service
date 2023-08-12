package me.anisekai.toshiko.modules.discord.tasks;

import me.anisekai.toshiko.modules.discord.JdaStore;
import me.anisekai.toshiko.data.Task;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.modules.discord.messages.embeds.AnimeEmbed;
import me.anisekai.toshiko.services.AnimeService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendAnnouncementTask implements Task {

    private final static Logger LOGGER = LoggerFactory.getLogger(SendAnnouncementTask.class);

    private final AnimeService service;
    private final TextChannel  channel;
    private final Role         role;
    private final Anime        anime;

    public SendAnnouncementTask(AnimeService service, JdaStore store, Anime anime) {

        this.service = service;
        this.channel = store.getAnnouncementChannel();
        this.role    = store.getAnnouncementRole();
        this.anime   = anime;
    }

    @Override
    public String getName() {

        return String.format("ANIME:%s:ANNOUNCE:CREATE", this.anime.getId());
    }

    @Override
    public void run() {

        LOGGER.info("Handling announcement task for anime {}...", this.anime.getId());

        MessageCreateBuilder mcb     = new MessageCreateBuilder();
        AnimeEmbed           message = new AnimeEmbed(this.anime, 0);

        message.setContent(String.format(
                "Hey %s ! Un anime est désormais disponible !",
                this.role.getAsMention()
        ));

        message.setShowButtons(true);
        message.getHandler().accept(mcb);

        LOGGER.debug("Sending announcement message...");
        Message discordMessage = this.channel.sendMessage(mcb.build()).complete();
        LOGGER.debug("Saving announcement message ID...");
        this.anime.setAnnounceMessage(discordMessage.getIdLong());
        this.service.getRepository().save(this.anime);
        LOGGER.info("Announcement task finished.");
    }

}
