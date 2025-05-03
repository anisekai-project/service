package me.anisekai.discord.tasks.anime.announcement;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import me.anisekai.discord.JDAStore;
import me.anisekai.discord.exceptions.tasks.UndefinedAnnouncementChannelException;
import me.anisekai.discord.exceptions.tasks.UndefinedAnnouncementRoleException;
import me.anisekai.discord.responses.messages.AnimeCardMessage;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.Interest;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.InterestService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.utils.DiscordUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class AnnouncementTask implements TaskExecutor {

    public static final String OPTION_ANIME = "anime";

    private final static Logger LOGGER = LoggerFactory.getLogger(AnnouncementTask.class);

    private final AnimeService    animeService;
    private final InterestService interestService;
    private final JDAStore        store;

    public AnnouncementTask(AnimeService animeService, InterestService interestService, JDAStore store) {

        this.animeService    = animeService;
        this.interestService = interestService;
        this.store           = store;
    }

    @Override
    public void execute(ITimedAction timer, AnisekaiJson params) {

        TextChannel      channel   = this.getAnnouncementChannel();
        Role             role      = this.getAnnouncementRole();
        Anime            anime     = this.animeService.fetch(params.getLong(OPTION_ANIME));
        List<Interest>   interests = this.interestService.getInterests(anime);
        AnimeCardMessage card      = new AnimeCardMessage(anime, interests, role);

        Optional<Message> optionalMessage = DiscordUtils.findExistingMessage(channel, anime);

        if (optionalMessage.isEmpty()) { // No message exists yet
            MessageCreateBuilder builder = new MessageCreateBuilder();
            card.getHandler().accept(builder);
            Message message = channel.sendMessage(builder.build()).complete();
            this.animeService.mod(anime.getId(), entity -> entity.setAnnouncementId(message.getIdLong()));
            return;
        }

        Message            message = optionalMessage.get();
        MessageEditBuilder builder = new MessageEditBuilder();
        card.getHandler().accept(builder);
        message.editMessage(builder.build()).complete();
    }

    @Override
    public boolean validateParams(AnisekaiJson params) {

        return params.has(OPTION_ANIME);
    }

    private TextChannel getAnnouncementChannel() {

        return this.store.getAnnouncementChannel().orElseThrow(UndefinedAnnouncementChannelException::new);
    }

    private Role getAnnouncementRole() {

        return this.store.getAnnouncementRole().orElseThrow(UndefinedAnnouncementRoleException::new);
    }

}
