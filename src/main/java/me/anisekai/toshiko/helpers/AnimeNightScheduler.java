package me.anisekai.toshiko.helpers;

import me.anisekai.toshiko.data.BookedAnimeNight;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.events.AnimeNightUpdateEvent;
import me.anisekai.toshiko.exceptions.animes.InvalidAnimeProgressException;
import me.anisekai.toshiko.exceptions.nights.OverlappingScheduleException;
import me.anisekai.toshiko.interfaces.AnimeNightMeta;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.*;

public class AnimeNightScheduler {

    private static final Logger                      LOGGER   = LoggerFactory.getLogger(AnimeNightScheduler.class);
    public final static  List<ScheduledEvent.Status> STATUSES = Arrays.asList(ScheduledEvent.Status.ACTIVE, ScheduledEvent.Status.SCHEDULED);

    private final ToshikoService service;

    public AnimeNightScheduler(ToshikoService service) {

        this.service = service;
    }

    public List<AnimeNightMeta> findDailySpot(Map<DayOfWeek, Anime> groupData) {

        List<AnimeNight> nights = this.service.getAnimeNightRepository().findAllByStatusIn(STATUSES);
        return this.findDailySpot(nights, groupData);
    }

    public <T extends AnimeNightMeta> List<AnimeNightMeta> findDailySpot(Collection<T> reservedSpots, Map<DayOfWeek, Anime> groupData) {

        List<AnimeNightMeta>       spots            = new ArrayList<>();
        Collection<AnimeNightMeta> newReservedSpots = new ArrayList<>(reservedSpots);

        ZonedDateTime now       = ZonedDateTime.now();
        ZonedDateTime scheduled = now.withHour(22).withMinute(30).withSecond(0).withNano(0);

        if (scheduled.isBefore(now)) {
            scheduled = scheduled.plusDays(1);
        }

        while (!groupData.isEmpty()) {

            DayOfWeek day = scheduled.getDayOfWeek();

            if (!groupData.containsKey(day)) {
                scheduled = scheduled.plusDays(1);
                continue;
            }

            Anime          anime          = groupData.get(day);
            OffsetDateTime offsetSchedule = scheduled.toOffsetDateTime();

            AnimeNightMeta meta = this.findLatestBefore(newReservedSpots, anime, scheduled.toOffsetDateTime())
                                      .map(spot -> BookedAnimeNight.after(spot, offsetSchedule, 1))
                                      .orElseGet(() -> BookedAnimeNight.with(anime, offsetSchedule, 1));


            if (this.canSchedule(newReservedSpots, meta)) {
                newReservedSpots.add(meta);
                spots.add(meta);

                if (meta.getLastEpisode() == anime.getTotal()) {
                    groupData.remove(day);
                }
            }

            scheduled = scheduled.plusDays(1);
        }

        return spots;
    }

    public <T extends AnimeNightMeta> Optional<T> findLatestBefore(Collection<T> spots, Anime anime, OffsetDateTime date) {

        LOGGER.debug("ANS [findLatestBefore] -> Last Meta Before '{}' (Anime={})", date, anime.getId());
        return spots.stream()
                    .filter(spot -> spot.getAnime().equals(anime))
                    .filter(spot -> spot.getStartDateTime().isBefore(date))
                    .max(Comparator.comparing(AnimeNightMeta::getStartDateTime));
    }

    public <T extends AnimeNightMeta> boolean fillWatchData(Collection<T> reservedSpots, AnimeNightMeta meta) {

        LOGGER.debug("ANS [fillWatchData] -> Set Watch Data for (Anime={},Start={})",
                meta.getAnime().getId(),
                meta.getStartDateTime()
        );

        Optional<T> latestSpot = this.findLatestBefore(reservedSpots, meta.getAnime(), meta.getStartDateTime());

        if (latestSpot.isPresent()) {
            LOGGER.debug("ANS [findLatestBefore] -> Set watch data based on a previous Meta...");
            T       spot       = latestSpot.get();
            boolean syncResult = meta.getFirstEpisode() != spot.getLastEpisode() + 1;
            meta.setFirstEpisode(spot.getLastEpisode() + 1);
            return syncResult;
        } else {
            LOGGER.debug("ANS [findLatestBefore] -> Set watch data based on Anime state...");
            boolean syncResult = meta.getFirstEpisode() != meta.getAnime().getWatched() + 1;
            meta.setFirstEpisode(meta.getAnime().getWatched() + 1);
            return syncResult;
        }
    }

    public <T extends AnimeNightMeta> AnimeNightMeta getNextSpot(Collection<T> reservedSpots, AnimeNightMeta available) {

        LOGGER.debug(
                "ANS [getNextSpot] -> Find Next Available Spot for (Anime={},Start={})",
                available.getAnime().getId(),
                available.getStartDateTime()
        );

        while (reservedSpots.stream().anyMatch(available::isColliding)) {
            available.setStartDateTime(available.getStartDateTime().plusDays(1));
        }

        LOGGER.debug(
                "ANS [getNextSpot] -> Spot Found: (Anime={},Start={})",
                available.getAnime().getId(),
                available.getStartDateTime()
        );

        this.fillWatchData(reservedSpots, available);
        return available;
    }

    public Collection<AnimeNightMeta> getNextSpots(Collection<? extends AnimeNightMeta> spots, Anime anime, OffsetTime startTime, long amountPerSpot, int maxGenerateAmount) {

        if (anime.getTotal() <= 0) {
            throw new InvalidAnimeProgressException();
        }

        LOGGER.debug(
                "ANS [getNextSpots] -> Finding multiple available slots for (Anime={},Start={},Amount={})",
                anime.getId(),
                startTime,
                amountPerSpot
        );

        Collection<AnimeNightMeta> availableSpots = new ArrayList<>();
        Collection<AnimeNightMeta> reservedSpots  = new ArrayList<>(spots);
        boolean                    canStillCreate = anime.getWatched() < anime.getTotal();

        while (canStillCreate && availableSpots.size() < maxGenerateAmount) {
            AnimeNightMeta availableSpot = this.getNextSpot(reservedSpots, BookedAnimeNight.with(anime, startTime, amountPerSpot));
            canStillCreate = availableSpot.getLastEpisode() < anime.getTotal();

            LOGGER.debug(
                    "ANS [getNextSpots] -> Found spot (Anime={},Start={})",
                    availableSpot.getAnime().getId(),
                    availableSpot.getStartDateTime()
            );

            reservedSpots.add(availableSpot);
            availableSpots.add(availableSpot);
        }

        LOGGER.debug("ANS [getNextSpots] -> Reserved {} spots", availableSpots.size());
        return availableSpots;
    }

    public boolean canSchedule(Collection<? extends AnimeNightMeta> spots, AnimeNightMeta meta) {

        return spots.stream().noneMatch(scheduled -> scheduled.isColliding(meta));
    }

    public boolean canSchedule(AnimeNightMeta meta) {

        LOGGER.debug(
                "ANS [getNextSpots] -> Checking if can schedule (Anime={},Start={})",
                meta.getAnime().getId(),
                meta.getStartDateTime()
        );

        return this.canSchedule(this.service.getAnimeNightRepository().findAllByStatusIn(STATUSES), meta);
    }

    public List<AnimeNight> calibrate() {

        List<AnimeNight> nights = this.service.getAnimeNightRepository().findAllByStatusIn(STATUSES);

        List<AnimeNight> editedNights = nights.stream().filter(night -> this.fillWatchData(nights, night)).toList();
        return this.service.getAnimeNightRepository().saveAll(editedNights);
    }

    public List<AnimeNight> calibrate(Anime anime) {

        LOGGER.debug("ANS [calibrate] -> Calibrating schedule for (Anime={})", anime.getId());
        List<AnimeNight> nights = this.service.getAnimeNightRepository().findAllByAnimeAndStatusIn(anime, STATUSES);

        List<AnimeNight> editedNights = nights.stream().filter(night -> this.fillWatchData(nights, night)).toList();
        return this.service.getAnimeNightRepository().saveAll(editedNights);
    }

    public AnimeNight schedule(Guild guild, AnimeNightMeta meta) {

        LOGGER.info(
                "ANS [schedule] -> Trying to schedule (Anime={}, Start={}, End={}, FirstEp={}, LastEp={})",
                meta.getAnime().getId(),
                meta.getStartDateTime(),
                meta.getEndDateTime(),
                meta.getFirstEpisode(),
                meta.getLastEpisode()
        );

        if (!this.canSchedule(meta)) {
            LOGGER.warn("ANS [schedule] -> Can't schedule this. Overlap detected.");
            throw new OverlappingScheduleException(meta.getAnime());
        }

        LOGGER.info("ANS [schedule] -> Saving into database...");
        AnimeNight event = this.service.getAnimeNightRepository().save(new AnimeNight(meta));

        this.calibrate(meta.getAnime()).stream()
            .map(night -> new AnimeNightUpdateEvent(this, guild, night))
            .forEach(this.service.getPublisher()::publishEvent);

        LOGGER.info("ANS [schedule] -> Finished.");
        return event;
    }

}
