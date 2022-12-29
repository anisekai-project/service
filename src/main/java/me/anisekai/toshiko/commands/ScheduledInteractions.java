package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.enums.SlashTarget;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.events.AnimeNightUpdateEvent;
import me.anisekai.toshiko.helpers.AnimeNightScheduler;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.helpers.responses.SimpleResponse;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@Component
@InteractionBean
public class ScheduledInteractions {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledInteractions.class);

    private final ToshikoService service;

    public ScheduledInteractions(ToshikoService service) {

        this.service = service;
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

        OffsetDateTime startDateTime = ZonedDateTime.now()
                                                    .withYear(scheduledAt.getYear())
                                                    .withMonth(scheduledAt.getMonthValue())
                                                    .withDayOfMonth(scheduledAt.getDayOfMonth())
                                                    .withHour(timeParam.getHour())
                                                    .withMinute(timeParam.getMinute())
                                                    .withSecond(0)
                                                    .withNano(0)
                                                    .toOffsetDateTime();

        LOGGER.info("[schedule/night] Trying to schedule (Anime={}, Day={}, Time={})", animeId, startDateTime, time);
        return this.service.schedule(anime, startDateTime, amount);
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/group">
    @Interact(
            name = "schedule/group",
            description = "Programme plusieurs scéances jusqu'à ce que tous les épisodes de l'anime soit plannifiés.",
            defer = true,
            target = SlashTarget.GUILD,
            options = {
                    @Option(
                            name = "anime",
                            description = "L'anime a regarder",
                            required = true,
                            type = OptionType.INTEGER,
                            autoComplete = true
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
                    )
            }
    )
    public SlashResponse scheduleAnimeNights(
            DiscordUser user,
            @Param("anime") long animeId,
            @Param("time") String time,
            @Param("amount") long amount
    ) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça !", false, false);
        }

        Anime         anime     = this.service.findAnime(animeId);
        ZonedDateTime today     = LocalDateTime.now().atZone(ZoneId.systemDefault());
        OffsetTime    timeParam = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")).atOffset(ZoneOffset.UTC);

        LOGGER.info("[schedule/anime] Trying to schedule group (Anime={}, Time={}, Amount={})", animeId, time, amount);
        int scheduledAmount = this.service.groupSchedule(anime, timeParam, amount).size();
        return new SimpleResponse(String.format("%s scéance(s) ont été programmé pour l'anime **%s**", scheduledAmount, anime.getName()), false, false);
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/simulcast">
    @Interact(
            name = "schedule/simulcast",
            description = "Programme en groupé des simulcasts (22h30)",
            target = SlashTarget.GUILD,
            defer = true,
            options = {
                    @Option(
                            name = "monday",
                            description = "Programme du Lundi",
                            autoComplete = true,
                            type = OptionType.INTEGER,
                            autoCompleteName = "anime"
                    ),
                    @Option(
                            name = "tuesday",
                            description = "Programme du Mardi",
                            autoComplete = true,
                            type = OptionType.INTEGER,
                            autoCompleteName = "anime"
                    ),
                    @Option(
                            name = "wednesday",
                            description = "Programme du Mercredi",
                            autoComplete = true,
                            type = OptionType.INTEGER,
                            autoCompleteName = "anime"
                    ),
                    @Option(
                            name = "thursday",
                            description = "Programme du Jeudi",
                            autoComplete = true,
                            type = OptionType.INTEGER,
                            autoCompleteName = "anime"
                    ),
                    @Option(
                            name = "friday",
                            description = "Programme du Vendredi",
                            autoComplete = true,
                            type = OptionType.INTEGER,
                            autoCompleteName = "anime"
                    ),
                    @Option(
                            name = "saturday",
                            description = "Programme du Samedi",
                            autoComplete = true,
                            type = OptionType.INTEGER,
                            autoCompleteName = "anime"
                    ),
                    @Option(
                            name = "sunday",
                            description = "Programme du Dimanche",
                            autoComplete = true,
                            type = OptionType.INTEGER,
                            autoCompleteName = "anime"
                    )
            }
    )
    public SlashResponse scheduleSimulcasts(
            DiscordUser user,
            @Param("monday") Long monday,
            @Param("tuesday") Long tuesday,
            @Param("wednesday") Long wednesday,
            @Param("thursday") Long thursday,
            @Param("friday") Long friday,
            @Param("saturday") Long saturday,
            @Param("sunday") Long sunday
    ) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça !", false, false);
        }

        Map<DayOfWeek, Anime> dayAnimeMap = new HashMap<>();
        // <editor-fold desc="Daily Anime Load">
        if (monday != null) {
            dayAnimeMap.put(DayOfWeek.MONDAY, this.service.findAnime(monday));
            LOGGER.info("[schedule/simulcast] Added anime {} for monday", monday);
        }

        if (tuesday != null) {
            dayAnimeMap.put(DayOfWeek.TUESDAY, this.service.findAnime(tuesday));
            LOGGER.info("[schedule/simulcast] Added anime {} for tuesday", tuesday);
        }

        if (wednesday != null) {
            dayAnimeMap.put(DayOfWeek.WEDNESDAY, this.service.findAnime(wednesday));
            LOGGER.info("[schedule/simulcast] Added anime {} for wednesday", wednesday);
        }

        if (thursday != null) {
            dayAnimeMap.put(DayOfWeek.THURSDAY, this.service.findAnime(thursday));
            LOGGER.info("[schedule/simulcast] Added anime {} for thursday", thursday);
        }

        if (friday != null) {
            dayAnimeMap.put(DayOfWeek.FRIDAY, this.service.findAnime(friday));
            LOGGER.info("[schedule/simulcast] Added anime {} for friday", friday);
        }

        if (saturday != null) {
            dayAnimeMap.put(DayOfWeek.SATURDAY, this.service.findAnime(saturday));
            LOGGER.info("[schedule/simulcast] Added anime {} for saturday", saturday);
        }

        if (sunday != null) {
            dayAnimeMap.put(DayOfWeek.SUNDAY, this.service.findAnime(sunday));
            LOGGER.info("[schedule/simulcast] Added anime {} for sunday", sunday);
        }
        //</editor-fold>

        List<AnimeNight> scheduled = this.service.schedule(dayAnimeMap);
        return new SimpleResponse(String.format("%s scéance(s) ont été programmées. Cela peut prendre un certain temps avant que toutes les scéances soient présentes sur Discord.", scheduled.size()), false, false);
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/calibrate">
    @Interact(
            name = "schedule/calibrate",
            description = "Parcours les évènements et recalibre les épisodes au besoin."
    )
    public SlashResponse calibrateSchedule(DiscordUser user, Guild guild) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça !", false, false);
        }

        AnimeNightScheduler scheduler = new AnimeNightScheduler(this.service);
        scheduler.calibrate().forEach(calibrated -> {
            this.service.getPublisher().publishEvent(new AnimeNightUpdateEvent(this, guild, calibrated));
        });

        return new SimpleResponse("La recalibration est en cours.", false, false);
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/refresh">
    @Interact(
            name = "schedule/refresh",
            description = "Actualise tout les évènements."
    )
    public SlashResponse refreshAllSchedule(DiscordUser user, Guild guild) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça !", false, false);
        }

        this.service.getAnimeNightRepository().findAll().stream()
                    .filter(night -> night.getStatus() == ScheduledEvent.Status.SCHEDULED)
                    .forEach(night -> {
                        this.service.getPublisher().publishEvent(new AnimeNightUpdateEvent(this, guild, night));
                    });

        return new SimpleResponse("L'actualisation est en cours.", false, false);
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/cleanup">
    @Interact(
            name = "schedule/cleanup",
            description = "Supprime les éléments superflus en base de données."
    )
    public SlashResponse cleanup(DiscordUser user, Guild guild) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça !", false, false);
        }

        List<ScheduledEvent> events = guild.getScheduledEvents();

        Predicate<AnimeNight> hasNotDiscordEvent = (night) -> events.stream()
                                                                    .noneMatch(ev -> Objects.equals(night.getEventId(), ev.getIdLong()));

        List<AnimeNight> nights = this.service.getAnimeNightRepository()
                                              .findAll()
                                              .stream()
                                              .filter(hasNotDiscordEvent)
                                              .toList();

        this.service.getAnimeNightRepository().deleteAll(nights);
        return new SimpleResponse(String.format("**%s** évènements ont été nettoyés.", nights.size()), false, false);
    }
    // </editor-fold>

}
