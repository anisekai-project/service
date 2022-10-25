package me.anisekai.toshiko.controller.response;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.enums.AnimeStatus;
import net.dv8tion.jda.api.entities.ScheduledEvent;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class RaspberryData {

    public static class ServerEvent {

        private final Anime          anime;
        private final long           amount;
        private final String         status;
        private final String         image;
        private final OffsetDateTime startTime;
        private final OffsetDateTime endTime;
        private final boolean        linked;

        public ServerEvent(AnimeNight animeNight, ScheduledEvent event) {

            this.anime  = animeNight.getAnime();
            this.amount = animeNight.getAmount();
            this.status = animeNight.getStatus().name();
            this.image  = event.getImageUrl();

            if (event != null) {
                this.startTime = event.getStartTime();
                this.endTime   = event.getEndTime();
                this.linked    = true;
            } else {
                this.startTime = null;
                this.endTime   = null;
                this.linked    = false;
            }
        }

        public Anime getAnime() {

            return this.anime;
        }

        public long getAmount() {

            return this.amount;
        }

        public String getImage() {

            return this.image;
        }

        public String getStatus() {

            return this.status;
        }

        public OffsetDateTime getStartTime() {

            return this.startTime;
        }

        public OffsetDateTime getEndTime() {

            return this.endTime;
        }

        public boolean isLinked() {

            return this.linked;
        }
    }

    private final List<Map<String, Object>> torrents;
    private final List<Anime>               animes;
    private final List<ServerEvent>         events;
    private final List<Map<String, Object>> status;

    public RaspberryData(List<Map<String, Object>> torrents, List<Anime> animes, List<ServerEvent> events) {

        this.torrents = torrents;
        this.animes   = animes;
        this.events   = events;
        this.status   = Stream.of(AnimeStatus.values()).map(AnimeStatus::asMap).toList();
    }

    public List<Map<String, Object>> getTorrents() {

        return this.torrents;
    }

    public List<Anime> getAnimes() {

        return this.animes;
    }

    public List<ServerEvent> getEvents() {

        return this.events;
    }

    public List<Map<String, Object>> getStatus() {

        return this.status;
    }
}
