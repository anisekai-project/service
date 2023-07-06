package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.messages.responses.SimpleResponse;
import me.anisekai.toshiko.services.AnimeNightService;
import me.anisekai.toshiko.services.AnimeService;
import me.anisekai.toshiko.utils.PermissionUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
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

    private final AnimeNightService service;
    private final AnimeService      animeService;

    public ScheduledInteractions(AnimeNightService service, AnimeService animeService) {

        this.service      = service;
        this.animeService = animeService;
    }

    private SlashResponse scheduleInterval(DiscordUser user, long animeId, CharSequence timeParam, long amount, CharSequence startingParam, Function<ZonedDateTime, ZonedDateTime> timeIncrement) {

        PermissionUtils.requirePrivileges(user);

        Anime anime = this.animeService.getAnime(animeId);

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

        return new SimpleResponse(String.format(
                "%s séance(s) ont été programmé pour l'anime **%s**",
                nights.size(),
                anime.getName()
        ), false, false);
    }

    // <editor-fold desc="@ schedule/daily">
    @Interact(
            name = "schedule/daily",
            description = Texts.SCHEDULE_DAILY__DESCRIPTION,
            defer = true,
            options = {
                    @Option(
                            name = "anime",
                            description = Texts.SCHEDULE_DAILY__OPTION_ANIME,
                            type = OptionType.INTEGER,
                            autoComplete = true,
                            required = true
                    ),
                    @Option(
                            name = "at",
                            description = Texts.SCHEDULE_DAILY__OPTION_AT,
                            type = OptionType.STRING,
                            required = true
                    ),
                    @Option(
                            name = "amount",
                            description = Texts.SCHEDULE_DAILY__OPTION_AMOUNT,
                            type = OptionType.INTEGER,
                            required = true
                    ),
                    @Option(
                            name = "starting",
                            description = Texts.SCHEDULE_DAILY__OPTION_STARTING,
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
            description = Texts.SCHEDULE_WEEKLY__DESCRIPTION,
            defer = true,
            options = {
                    @Option(
                            name = "anime",
                            description = Texts.SCHEDULE_WEEKLY__OPTION_ANIME,
                            type = OptionType.INTEGER,
                            autoComplete = true,
                            required = true
                    ),
                    @Option(
                            name = "at",
                            description = Texts.SCHEDULE_WEEKLY__OPTION_AT,
                            type = OptionType.STRING,
                            required = true
                    ),
                    @Option(
                            name = "amount",
                            description = Texts.SCHEDULE_WEEKLY__OPTION_AMOUNT,
                            type = OptionType.INTEGER,
                            required = true
                    ),
                    @Option(
                            name = "starting",
                            description = Texts.SCHEDULE_WEEKLY__OPTION_STARTING,
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
            description = Texts.SCHEDULE_ANIME__DESCRIPTION,
            defer = true,
            options = {
                    @Option(
                            name = "anime",
                            description = Texts.SCHEDULE_ANIME__OPTION_ANIME,
                            type = OptionType.INTEGER,
                            autoComplete = true,
                            required = true
                    ),
                    @Option(
                            name = "at",
                            description = Texts.SCHEDULE_ANIME__OPTION_AT,
                            type = OptionType.STRING,
                            required = true
                    ),
                    @Option(
                            name = "amount",
                            description = Texts.SCHEDULE_ANIME__OPTION_AMOUNT,
                            type = OptionType.INTEGER,
                            required = true
                    ),
                    @Option(
                            name = "date",
                            description = Texts.SCHEDULE_ANIME__OPTION_DATE,
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse scheduleAnimeNight(
            DiscordUser user,
            @Param("anime") long animeId,
            @Param("at") String time,
            @Param("date") String day,
            @Param("amount") long amount
    ) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça !", false, false);
        }

        Anime     anime       = this.animeService.getAnime(animeId);
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

        return this.service.schedule(anime, startDateTime, amount);
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/calibrate">
    @Interact(
            name = "schedule/calibrate",
            description = Texts.SCHEDULE_CALIBRATE__DESCRIPTION,
            defer = true
    )
    public SlashResponse calibrateSchedule(DiscordUser user) {

        PermissionUtils.requirePrivileges(user);

        int updated = 0;

        List<Anime> animes = this.service.getRepository()
                                         .findAllByStatusIn(AnimeNight.WATCHABLE)
                                         .stream()
                                         .map(AnimeNight::getAnime)
                                         .distinct().toList();

        for (Anime anime : animes) {
            updated += this.service.calibrate(anime).size();
        }

        return new SimpleResponse(String.format("**%s** évènements ont été mis à jour.", updated), false, false);
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/delay">
    @Interact(
            name = "schedule/delay",
            description = Texts.SCHEDULE_DELAY__DESCRIPTION,
            defer = true,
            options = {
                    @Option(
                            name = "delay",
                            description = Texts.SCHEDULE_DELAY__OPTION_DELAY,
                            required = true,
                            type = OptionType.INTEGER
                    )
            }
    )
    public SlashResponse delaySchedule(DiscordUser user, @Param("delay") long delay) {

        PermissionUtils.requirePrivileges(user);

        int delayedAmount = this.service.delay(delay).size();
        return new SimpleResponse(String.format("**%s** évènements ont été décalés.", delayedAmount), false, false);
    }
    // </editor-fold>
}
