package me.anisekai.modules.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.chiya.interfaces.IUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.shizue.enums.BroadcastFrequency;
import me.anisekai.modules.shizue.interfaces.entities.IBroadcast;
import me.anisekai.modules.linn.services.data.AnimeDataService;
import me.anisekai.modules.shizue.services.data.BroadcastDataService;
import me.anisekai.globals.utils.DateTimeUtils;
import me.anisekai.modules.toshiko.Texts;
import me.anisekai.modules.toshiko.annotations.InteractionBean;
import me.anisekai.modules.toshiko.messages.responses.SimpleResponse;
import me.anisekai.modules.toshiko.utils.PermissionUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
@InteractionBean
public class BroadcastInteraction {

    private final BroadcastDataService service;
    private final AnimeDataService     animeService;

    public BroadcastInteraction(BroadcastDataService service, AnimeDataService animeService) {

        this.service      = service;
        this.animeService = animeService;
    }

    // <editor-fold desc="@ broadcast/schedule">
    @Interact(
            name = "broadcast/schedule",
            description = Texts.BROADCAST_SCHEDULE__DESCRIPTION,
            defer = true,
            options = {
                    @Option(
                            name = "anime",
                            description = Texts.BROADCAST_SCHEDULE__OPTION_ANIME,
                            type = OptionType.INTEGER,
                            autoComplete = true,
                            required = true
                    ),
                    @Option(
                            name = "time",
                            description = Texts.BROADCAST_SCHEDULE__OPTION_TIME,
                            type = OptionType.STRING,
                            required = true
                    ),
                    @Option(
                            name = "amount",
                            description = Texts.BROADCAST_SCHEDULE__OPTION_AMOUNT,
                            type = OptionType.INTEGER,
                            required = true
                    ),
                    @Option(
                            name = "frequency",
                            description = Texts.BROADCAST_SCHEDULE__OPTION_FREQUENCY,
                            type = OptionType.STRING,
                            autoComplete = true
                    ),
                    @Option(
                            name = "starting",
                            description = Texts.BROADCAST_SCHEDULE__OPTION_STARTING,
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse runBroadcastSchedule(
            IUser sender,
            @Param("anime") long animeId,
            @Param("frequency") String frequencyName,
            @Param("time") String timeParam,
            @Param("amount") long amount,
            @Param("starting") String startingParam
    ) {

        PermissionUtils.requirePrivileges(sender);

        Anime              anime      = this.animeService.fetch(animeId);
        BroadcastFrequency frequency  = BroadcastFrequency.from(frequencyName);
        ZonedDateTime      scheduleAt = DateTimeUtils.of(timeParam, startingParam);

        int scheduledAmount;
        if (frequency.hasDateModifier()) {
            List<IBroadcast> broadcasts = this.service.schedule(anime, scheduleAt, amount, frequency.getDateModifier());
            scheduledAmount = broadcasts.size();
        } else {
            this.service.schedule(anime, scheduleAt, amount);
            scheduledAmount = 1;
        }

        if (scheduledAmount == 0) {
            return new SimpleResponse("Aucune séance n'a été programmée.", false, false);
        } else if (scheduledAmount == 1) {
            return new SimpleResponse(
                    String.format(
                            "**1** séance a été programmée pour l'anime [%s](%s)",
                            anime.getName(),
                            anime.getLink()
                    ), false, false);
        } else {
            return new SimpleResponse(
                    String.format(
                            "**%s** séances ont été programmées pour l'anime [%s](%s)",
                            scheduledAmount,
                            anime.getName(),
                            anime.getLink()
                    ), false, false);
        }
    }
    // </editor-fold>

    // <editor-fold desc="broadcast/calibrate">
    @Interact(
            name = "broadcast/calibrate",
            description = Texts.BROADCAST_CALIBRATE__DESCRIPTION,
            defer = true
    )
    public SlashResponse runBroadcastCalibration(
            IUser sender
    ) {

        PermissionUtils.requirePrivileges(sender);

        List<Anime>      animes     = this.animeService.fetchAll(repository -> repository.findAllByStatusIn(AnimeStatus.getSchedulable()));
        List<IBroadcast> calibrated = this.service.calibrate(animes);

        if (calibrated.isEmpty()) {
            return new SimpleResponse("Aucune séance n'a été modifiée.", false, false);
        } else if (calibrated.size() == 1) {
            return new SimpleResponse("**1** séance a été modifiée.", false, false);
        } else {
            return new SimpleResponse(
                    String.format(
                            "**%s** séances ont été modifiées.",
                            calibrated.size()
                    ), false, false);
        }
    }
    // </editor-fold>

    // <editor-fold desc="@ broadcast/delay">

    @Interact(
            name = "broadcast/delay",
            description = Texts.BROADCAST_DELAY__DESCRIPTION,
            defer = true,
            options = {
                    @Option(
                            name = "delay",
                            description = Texts.BROADCAST_DELAY__OPTION_DELAY,
                            type = OptionType.STRING,
                            required = true
                    ),
                    @Option(
                            name = "range",
                            description = Texts.BROADCAST_DELAY__OPTION_RANGE,
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse runBroadcastDelay(
            IUser sender,
            @Param("delay") String delayParam,
            @Param("range") String rangeParam
    ) {

        PermissionUtils.requirePrivileges(sender);

        String rangeString = Optional.ofNullable(rangeParam).orElse("6h");

        Duration delay = DateTimeUtils.toDuration(delayParam);
        Duration range = DateTimeUtils.toDuration(rangeString);

        List<IBroadcast> delayed = this.service.delay(range, delay);

        if (delayed.isEmpty()) {
            return new SimpleResponse("Aucune séance n'a été reprogrammée.", false, false);
        } else if (delayed.size() == 1) {
            return new SimpleResponse("**1** séance a été modifiée.", false, false);
        } else {
            return new SimpleResponse(
                    String.format(
                            "**%s** séances ont été reprogrammées.",
                            delayed.size()
                    ), false, false);
        }
    }

    // </editor-fold>

    // <editor-fold desc="@ broadcast/refresh">

    @Interact(
            name = "broadcast/refresh",
            description = Texts.BROADCAST_REFRESH__DESCRIPTION
    )
    public SlashResponse runBroadcastRefresh(DiscordUser sender) {

        PermissionUtils.requirePrivileges(sender);
        this.service.askBroadcastRefresh();
        return new SimpleResponse("Les évènements serveur vont être actualisés sous peu.", false, false);
    }

    // </editor-fold>
}
