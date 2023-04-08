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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

@Component
@InteractionBean
public class ScheduledInteractions {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledInteractions.class);

    private final ToshikoService service;

    public ScheduledInteractions(ToshikoService service) {

        this.service = service;
    }

    private SlashResponse scheduleInterval(DiscordUser user, long animeId, CharSequence timeParam, long amount, CharSequence startingParam, Function<ZonedDateTime, ZonedDateTime> timeIncrement) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça !", false, false);
        }

        Anime anime = this.service.findAnime(animeId);

        if (anime.getTotal() < 1) {
            return new SimpleResponse("Impossible de programmer en masse cet anime.", false, false);
        }

        LocalTime     time         = LocalTime.parse(timeParam, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime startingFrom = LocalDate.now().atTime(time);

        if (startingParam != null) {
            startingFrom = LocalDate.parse(startingParam, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                    .atTime(time);
        }

        ZonedDateTime scheduleAt = ZonedDateTime.now()
                                                .withYear(startingFrom.getYear())
                                                .withMonth(startingFrom.getMonthValue())
                                                .withDayOfMonth(startingFrom.getDayOfMonth())
                                                .withHour(startingFrom.getHour())
                                                .withMinute(startingFrom.getMinute())
                                                .withSecond(0)
                                                .withNano(0);

        List<AnimeNight> nights = this.service.scheduleAll(anime, scheduleAt, amount, timeIncrement);

        return new SimpleResponse(String.format("%s scéance(s) ont été programmé pour l'anime **%s**", nights.size(), anime.getName()), false, false);
    }

    // <editor-fold desc="@ schedule/daily">
    @Interact(
            name = "schedule/daily",
            description = "Schedule an anime in a daily fashion",
            defer = true,
            options = {
                    @Option(
                            name = "anime",
                            description = "The anime to schedule",
                            type = OptionType.INTEGER,
                            autoComplete = true,
                            required = true
                    ),
                    @Option(
                            name = "at",
                            description = "At which hour the anime should be scheduled everyday. (HH:mm)",
                            type = OptionType.STRING,
                            required = true
                    ),
                    @Option(
                            name = "amount",
                            description = "The amount of episode to watch daily.",
                            type = OptionType.INTEGER,
                            required = true
                    ),
                    @Option(
                            name = "starting",
                            description = "When the daily scheduling should start (dd/MM/yyyy)",
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse scheduleDaily(
            DiscordUser user,
            @Param("anime") long animeId,
            @Param("at") String timeParam,
            @Param("amount") long amount,
            @Param("starting") String startingParam
    ) {

        return this.scheduleInterval(user, animeId, timeParam, amount, startingParam, date -> date.plusDays(1));
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/weekly">
    @Interact(
            name = "schedule/weekly",
            description = "Schedule an anime in a weekly fashion",
            defer = true,
            options = {
                    @Option(
                            name = "anime",
                            description = "The anime to schedule",
                            type = OptionType.INTEGER,
                            autoComplete = true,
                            required = true
                    ),
                    @Option(
                            name = "at",
                            description = "At which hour the anime should be scheduled everyday. (HH:mm)",
                            type = OptionType.STRING,
                            required = true
                    ),
                    @Option(
                            name = "amount",
                            description = "The amount of episode to watch daily.",
                            type = OptionType.INTEGER,
                            required = true
                    ),
                    @Option(
                            name = "starting",
                            description = "When the daily scheduling should start (dd/MM/yyyy)",
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse scheduleWeekly(
            DiscordUser user,
            @Param("anime") long animeId,
            @Param("at") String timeParam,
            @Param("amount") long amount,
            @Param("starting") String startingParam
    ) {

        return this.scheduleInterval(user, animeId, timeParam, amount, startingParam, date -> date.plusDays(7));
    }
    // </editor-fold>

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
                            name = "amount",
                            description = "Nombre d'épisode pour la scéance",
                            type = OptionType.INTEGER,
                            required = true
                    ),
                    @Option(
                            name = "day",
                            description = "Jour de visionnage",
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse scheduleAnimeNight(
            DiscordUser user,
            @Param("anime") long animeId,
            @Param("time") String time,
            @Param("day") String day,
            @Param("amount") long amount
    ) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça !", false, false);
        }

        Anime     anime       = this.service.findAnime(animeId);
        LocalTime timeParam   = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDate scheduledAt = LocalDate.now();

        if (day != null) {
            scheduledAt = LocalDate.parse(day, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        ZonedDateTime startDateTime = ZonedDateTime.now()
                                                   .withYear(scheduledAt.getYear())
                                                   .withMonth(scheduledAt.getMonthValue())
                                                   .withDayOfMonth(scheduledAt.getDayOfMonth())
                                                   .withHour(timeParam.getHour())
                                                   .withMinute(timeParam.getMinute())
                                                   .withSecond(0)
                                                   .withNano(0);

        LOGGER.info("[schedule/night] Trying to schedule (Anime={}, Day={}, Time={})", animeId, startDateTime, time);
        return this.service.schedule(anime, startDateTime, amount);
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/calibrate">
    @Interact(
            name = "schedule/calibrate",
            description = "Recalibre tous les évènements programmés",
            defer = true
    )
    public SlashResponse calibrateSchedule(DiscordUser user) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça !", false, false);
        }

        int updateCount = this.service.calibrate();
        return new SimpleResponse(String.format("**%s** évènements ont été mis à jour.", updateCount), false, false);
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/delay">
    @Interact(
            name = "schedule/delay",
            description = "Delay the schedule",
            defer = true,
            options = {
                    @Option(
                            name = "delay",
                            description = "The amount of time (in minutes) that the event should be delayed",
                            required = true,
                            type = OptionType.INTEGER
                    )
            }
    )
    public SlashResponse delaySchedule(DiscordUser user, @Param("delay") long delay) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça !", false, false);
        }

        int delayedAmount = this.service.delay(delay);

        if (delayedAmount == -1) {
            return new SimpleResponse("Impossible de décaler les évènements.", false, false);
        }

        return new SimpleResponse(String.format("**%s** évènements ont été décalés.", delayedAmount), false, false);
    }
    // </editor-fold>
}
