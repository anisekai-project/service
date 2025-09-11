package fr.anisekai.discord.listeners;

import fr.anisekai.discord.JDAStore;
import fr.anisekai.discord.responses.embeds.CalibrationEmbed;
import fr.anisekai.server.entities.Broadcast;
import fr.anisekai.server.services.BroadcastService;
import fr.anisekai.wireless.api.plannifier.data.CalibrationResult;
import fr.anisekai.wireless.remote.enums.BroadcastStatus;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DiscordListener extends ListenerAdapter {

    private final JDAStore         store;
    private final BroadcastService service;

    public DiscordListener(JDAStore store, BroadcastService service) {

        this.store   = store;
        this.service = service;
    }

    private void cancel(ScheduledEvent event) {

        Optional<Broadcast> optionalBroadcast = this.service.find(event);

        if (optionalBroadcast.isPresent()) {
            Broadcast broadcast = optionalBroadcast.get();
            this.service.cancel(broadcast);

            CalibrationResult calibrate = this.service.createScheduler().calibrate();

            this.store.getAuditChannel().ifPresent(channel -> {
                MessageCreateBuilder mcb   = new MessageCreateBuilder();
                CalibrationEmbed     embed = new CalibrationEmbed();
                embed.setCalibrationResult(broadcast, calibrate);
                mcb.setEmbeds(embed.build());
                channel.sendMessage(mcb.build()).queue();
            });
        }
    }

    @Override
    public void onScheduledEventDelete(ScheduledEventDeleteEvent event) {

        this.cancel(event.getScheduledEvent());
    }

    @Override
    public void onScheduledEventUpdateStatus(ScheduledEventUpdateStatusEvent event) {

        Optional<Broadcast> optionalBroadcast = this.service.find(event.getScheduledEvent());
        if (optionalBroadcast.isEmpty()) {
            return;
        }

        Broadcast broadcast = optionalBroadcast.get();

        switch (event.getNewStatus()) {
            case ACTIVE -> this.service.mod(
                    broadcast.getId(), entity -> entity.setStatus(BroadcastStatus.ACTIVE)
            );
            case COMPLETED -> this.service.mod(
                    broadcast.getId(), entity -> entity.setStatus(BroadcastStatus.COMPLETED)
            );

            case CANCELED -> this.cancel(event.getScheduledEvent());
        }
    }

}
