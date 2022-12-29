package me.anisekai.toshiko.entities;

import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.interfaces.AnimeNightMeta;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.utils.messages.MessageRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.function.Consumer;

@Entity
public class AnimeNight implements AnimeNightMeta, SlashResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private Long eventId;

    @ManyToOne(optional = false)
    private Anime anime;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private long firstEpisode;

    @Column(nullable = false)
    private long lastEpisode;

    @Column
    @Enumerated(EnumType.STRING)
    private ScheduledEvent.Status status;

    @Nullable
    private String imageUrl;

    @Column(nullable = false)
    @NotNull
    private OffsetDateTime startDateTime;

    @Column(nullable = false)
    @NotNull
    private OffsetDateTime endDateTime;

    public AnimeNight() {}

    /**
     * Create a new {@link AnimeNight}.
     *
     * @param meta
     *         An instance of {@link AnimeNightMeta} containing information about episode to watch.
     */
    public AnimeNight(AnimeNightMeta meta) {

        this.anime         = meta.getAnime();
        this.amount        = meta.getAmount();
        this.firstEpisode  = meta.getFirstEpisode();
        this.lastEpisode   = meta.getLastEpisode();
        this.startDateTime = meta.getStartDateTime();
        this.endDateTime   = meta.getEndDateTime();
    }

    public long getId() {

        return this.id;
    }

    public Long getEventId() {

        return this.eventId;
    }

    public void setEventId(Long eventId) {

        this.eventId = eventId;
    }

    public Anime getAnime() {

        return this.anime;
    }

    public void setAnime(Anime anime) {

        this.anime = anime;
    }

    public long getAmount() {

        return this.amount;
    }

    public void setAmount(long amount) {

        this.amount = amount;
    }

    public long getFirstEpisode() {

        return this.firstEpisode;
    }

    public void setFirstEpisode(long firstEpisode) {

        this.firstEpisode = firstEpisode;
        this.lastEpisode = firstEpisode + (this.getAmount() - 1);
    }

    public long getLastEpisode() {

        return this.lastEpisode;
    }

    public void setLastEpisode(long lastEpisode) {

        this.lastEpisode = lastEpisode;
    }

    public ScheduledEvent.@NotNull Status getStatus() {

        return this.status;
    }

    public void setStatus(ScheduledEvent.@NotNull Status status) {

        this.status = status;
    }

    public @Nullable String getImageUrl() {

        return this.imageUrl;
    }

    public void setImageUrl(@Nullable String imageUrl) {

        this.imageUrl = imageUrl;
    }

    @Override
    public @NotNull OffsetDateTime getStartDateTime() {

        return this.startDateTime;
    }

    @Override
    public void setStartDateTime(@NotNull OffsetDateTime startDateTime) {

        this.startDateTime = startDateTime;
        long openingEndingDuration = (this.getAmount() - 1) * 3; // OP/ED usually 1m30 each
        long totalWatchTime        = this.getAmount() * this.getAnime().getTotal();

        this.endDateTime = this.startDateTime.plusMinutes(totalWatchTime - openingEndingDuration);
    }

    @Override
    public @NotNull OffsetDateTime getEndDateTime() {

        return this.endDateTime;
    }

    @Override
    public void setEndDateTime(@NotNull OffsetDateTime endDateTime) {

        this.endDateTime = endDateTime;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        //noinspection ChainOfInstanceofChecks
        if (o instanceof AnimeNight other) {
            return other.getId() == this.getId();
        } else if (o instanceof ScheduledEvent other) {
            return Objects.equals(this.getEventId(), other.getIdLong());
        }
        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId());
    }

    @Override
    public Consumer<MessageRequest<?>> getHandler() {

        return (mr) -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(this.getAnime().getName(), this.getAnime().getLink());
            builder.setDescription("La scéance a bien été programmée.");
            builder.addField("Épisode(s)", this.asEventDescription(), true);

            mr.setEmbeds(builder.build());
        };
    }

    @Override
    public boolean isEphemeral() {

        return false;
    }
}
