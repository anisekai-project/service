package me.anisekai.discord.listeners;

import me.anisekai.api.plannifier.data.CalibrationResult;
import me.anisekai.discord.JDAStore;
import me.anisekai.discord.responses.embeds.CalibrationEmbed;
import me.anisekai.server.entities.Broadcast;
import me.anisekai.server.enums.BroadcastStatus;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.BroadcastService;
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
    private final AnimeService     animeService;

    public DiscordListener(JDAStore store, BroadcastService service, AnimeService animeService) {

        this.store        = store;
        this.service      = service;
        this.animeService = animeService;
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
            case ACTIVE -> {
                Broadcast updated = this.service.mod(
                        broadcast.getId(), entity -> {
                            entity.setStatus(BroadcastStatus.ACTIVE);
                        }
                );

                this.animeService.mod(
                        updated.getWatchTarget().getId(),
                        this.animeService.defineWatching()
                );
            }
            case COMPLETED -> {
                Broadcast updated = this.service.mod(
                        broadcast.getId(), entity -> {
                            entity.setStatus(BroadcastStatus.ACTIVE);
                        }
                );

                this.animeService.mod(
                        updated.getWatchTarget().getId(),
                        this.animeService.defineScheduleProgress(updated)
                );
            }

            case CANCELED -> {
                this.cancel(event.getScheduledEvent());
            }
        }
    }

}
