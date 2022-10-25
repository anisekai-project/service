package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.helpers.responses.SimpleResponse;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
@InteractionBean
public class ScheduledInteractions {

    private final ToshikoService toshikoService;

    public ScheduledInteractions(ToshikoService toshikoService) {

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
                            name = "date",
                            description = "Date de visionnage",
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
            @Param("date") long date,
            @Param("amount") long amount
    ) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça !", false, false);
        }

        Anime         anime     = this.toshikoService.findAnime(animeId);
        LocalDateTime today     = LocalDateTime.now();
        LocalTime     timeParam = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));

        LocalDateTime scheduledDate = today.withHour(timeParam.getHour())
                                           .withMinute(timeParam.getMinute())
                                           .withSecond(0)
                                           .withNano(0)
                                           .plusDays(date);

        AnimeNight night = this.toshikoService.schedule(anime, scheduledDate, amount);
        return new SimpleResponse(String.format("La scéance pour l'anime **%s** a bien été programmée.", night.getAnime()
                                                                                                              .getName()), false, false);
    }
    // </editor-fold>

}
