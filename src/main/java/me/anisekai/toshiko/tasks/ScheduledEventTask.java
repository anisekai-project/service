package me.anisekai.toshiko.tasks;

import me.anisekai.toshiko.entities.ScheduledEvent;
import me.anisekai.toshiko.helpers.JDAStore;
import me.anisekai.toshiko.helpers.embeds.ScheduledEventEmbed;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduledEventTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduledEventTask.class);

    private final ToshikoService toshikoService;
    private final DelayedTask    task;
    private final JDAStore       store;

    @Value("${toshiko.anime.schedule.channel}")
    private long toshikoAnimeScheduleChannel;

    public ScheduledEventTask(ToshikoService toshikoService, DelayedTask task, JDAStore store) {

        this.toshikoService = toshikoService;
        this.task           = task;
        this.store          = store;
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void execute() {

        List<ScheduledEvent> events = this.toshikoService.getNonNotifiedEvents();

        for (ScheduledEvent event : events) {
            this.task.queue(String.format("EV:%s", event.getId()), () -> {
                ScheduledEventEmbed  eventEmbed           = new ScheduledEventEmbed(event);
                TextChannel          channel              = this.getChannel();
                MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
                eventEmbed.getHandler().accept(messageCreateBuilder);
                channel.sendMessage(messageCreateBuilder.build()).complete();
            }, (ex) -> LOGGER.error("Unable to send scheduled event.", ex));
            this.toshikoService.setNotified(event);
        }

    }

    private TextChannel getChannel() {

        return this.store.getInstance()
                         .map(jda -> jda.getTextChannelById(this.toshikoAnimeScheduleChannel))
                         .orElseThrow(() -> new IllegalStateException("Wololo, le channel même qu'il est pas trouvéééééééé"));
    }
}
