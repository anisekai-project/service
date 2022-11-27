package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.enums.SlashTarget;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.exceptions.nights.OverlappingScheduleException;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.helpers.responses.SimpleResponse;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@InteractionBean
public class ScheduledInteractions {

    private final ToshikoService toshikoService;

    public ScheduledInteractions(ToshikoService toshikoService) {

        this.toshikoService = toshikoService;
    }

    // <editor-fold desc="@ schedule/night">
    @Interact(
            name = "schedule/night",
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

        if (!this.toshikoService.canSchedule(anime, scheduledDate, amount)) {
            throw new OverlappingScheduleException(anime);
        }

        this.toshikoService.schedule(anime, scheduledDate, amount);
        return new SimpleResponse(String.format("La scéance pour l'anime **%s** a bien été programmée.", anime.getName()), false, false);
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/anime">
    @Interact(
            name = "schedule/anime",
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

        Anime         anime     = this.toshikoService.findAnime(animeId);
        ZonedDateTime today     = LocalDateTime.now().atZone(ZoneId.systemDefault());
        LocalTime     timeParam = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));

        ZonedDateTime scheduledDate = today.withHour(timeParam.getHour())
                                           .withMinute(timeParam.getMinute())
                                           .withSecond(0)
                                           .withNano(0);

        if (scheduledDate.isBefore(today)) {
            scheduledDate = scheduledDate.plusDays(1);
        }

        long effectiveWatch = anime.getWatched();

        for (AnimeNight night : this.toshikoService.retrieveAllAnimeNightsBefore(scheduledDate, anime)) {
            effectiveWatch += night.getAmount();
        }

        long amountToSchedule = anime.getTotal() - effectiveWatch;

        if (amountToSchedule <= 0) {
            return new SimpleResponse("Cet anime ne peut plus être programmé.", false, true);
        }

        List<AnimeNight> scheduled = new ArrayList<>();
        while (amountToSchedule > 0) {
            long loopAmount = amountToSchedule >= amount ? amount : amountToSchedule;

            if (this.toshikoService.canSchedule(anime, scheduledDate, loopAmount)) {
                scheduled.add(this.toshikoService.schedule(anime, scheduledDate, loopAmount));
                amountToSchedule -= loopAmount;
            }

            scheduledDate = scheduledDate.plusDays(1);
        }

        return new SimpleResponse(String.format("%s scéance(s) ont été programmé pour l'anime **%s**", scheduled.size(), anime.getName()), false, false);
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
            dayAnimeMap.put(DayOfWeek.MONDAY, this.toshikoService.findAnime(monday));
        }

        if (tuesday != null) {
            dayAnimeMap.put(DayOfWeek.TUESDAY, this.toshikoService.findAnime(tuesday));
        }

        if (wednesday != null) {
            dayAnimeMap.put(DayOfWeek.WEDNESDAY, this.toshikoService.findAnime(wednesday));
        }

        if (thursday != null) {
            dayAnimeMap.put(DayOfWeek.THURSDAY, this.toshikoService.findAnime(thursday));
        }

        if (friday != null) {
            dayAnimeMap.put(DayOfWeek.FRIDAY, this.toshikoService.findAnime(friday));
        }

        if (saturday != null) {
            dayAnimeMap.put(DayOfWeek.SATURDAY, this.toshikoService.findAnime(saturday));
        }

        if (sunday != null) {
            dayAnimeMap.put(DayOfWeek.SUNDAY, this.toshikoService.findAnime(sunday));
        }
        //</editor-fold>

        ZonedDateTime now       = LocalDateTime.now().atZone(ZoneId.systemDefault());
        ZonedDateTime scheduled = now.withHour(22).withMinute(30);

        if (scheduled.isBefore(now)) {
            scheduled = scheduled.plusDays(1);
        }

        for (int i = 0 ; i < 7 ; i++) {
            DayOfWeek day = scheduled.getDayOfWeek();
            if (dayAnimeMap.containsKey(day)) {
                Anime anime = dayAnimeMap.get(day);
                if (this.toshikoService.canSchedule(anime, scheduled, 1)) {
                    this.toshikoService.schedule(anime, scheduled, 1);
                }
            }

            scheduled = scheduled.plusDays(1);
        }

        return new SimpleResponse("Les scéances ont été programmée.", false, false);
    }
    // </editor-fold>
}
