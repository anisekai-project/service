package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.exceptions.nights.OverlappingScheduleException;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.helpers.responses.SimpleResponse;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Component
@InteractionBean
public class ScheduledInteractions {

    private final ApplicationEventPublisher publisher;
    private final ToshikoService            toshikoService;

    public ScheduledInteractions(ApplicationEventPublisher publisher, ToshikoService toshikoService) {

        this.publisher      = publisher;
        this.toshikoService = toshikoService;
    }

    // <editor-fold desc="@ schedule/anime">
    @Interact(
            name = "schedule/anime",
            description = "Programme une soirée animé.",
            defer = true,
            options = {
                    @Option(
                            name = "anime",
                            description = "L'anime a regarder",
                            type = OptionType.INTEGER,
                            autoComplete = true,
                            required = true
                    ),
                    @Option(
                            name = "time",
                            description = "Heure de visionnage",
                            type = OptionType.STRING,
                            required = true
                    ),
                    @Option(
                            name = "day",
                            description = "Jour de visionnage",
                            type = OptionType.INTEGER,
                            autoComplete = true,
                            required = true
                    ),
                    @Option(
                            name = "amount",
                            description = "Nombre d'épisode pour la scéance",
                            type = OptionType.INTEGER,
                            required = true
                    )
            }
    )
    public SlashResponse scheduleAnimeNight(
            DiscordUser user,
            @Param("anime") long animeId,
            @Param("time") String time,
            @Param("day") Long day,
            @Param("amount") long amount
    ) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça !", false, false);
        }

        Anime         anime     = this.toshikoService.findAnime(animeId);
        ZonedDateTime today     = LocalDateTime.now().atZone(ZoneId.systemDefault());
        LocalTime     timeParam = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        DayOfWeek     dayOfWeek = DayOfWeek.of(day.intValue());

        ZonedDateTime scheduledDate = today.withHour(timeParam.getHour())
                                           .withMinute(timeParam.getMinute())
                                           .withSecond(0)
                                           .withNano(0);

        while (scheduledDate.getDayOfWeek() != dayOfWeek) {
            scheduledDate = scheduledDate.plusDays(1);
        }

        if (!this.toshikoService.canSchedule(scheduledDate, amount)) {
            throw new OverlappingScheduleException(anime);
        }

        this.toshikoService.schedule(anime, scheduledDate, amount);
        return new SimpleResponse(String.format("La scéance pour l'anime **%s** a bien été programmée.", anime.getName()), false, false);
    }
    // </editor-fold>

}
