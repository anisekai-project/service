package me.anisekai.toshiko.services;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.events.interest.InterestUpdatedEvent;
import me.anisekai.toshiko.repositories.InterestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class InterestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterestService.class);

    private final InterestRepository        repository;
    private final ApplicationEventPublisher publisher;

    public InterestService(InterestRepository repository, ApplicationEventPublisher publisher) {

        this.repository = repository;
        this.publisher  = publisher;
    }

    /**
     * Retrieve the {@link InterestLevel} of a {@link DiscordUser} for a specific {@link Anime}.
     *
     * @param anime
     *         The {@link Anime} for which the {@link InterestLevel} should be fetched
     * @param user
     *         The {@link DiscordUser} to which the {@link InterestLevel} belongs.
     *
     * @return The {@link InterestLevel}, or {@link InterestLevel#NEUTRAL} if no {@link Interest} was found.
     */
    public InterestLevel getInterestLevel(Anime anime, DiscordUser user) {

        return this.repository.findByAnimeAndUser(anime, user)
                              .map(Interest::getLevel)
                              .orElse(InterestLevel.NEUTRAL);
    }

    /**
     * Define the {@link InterestLevel} of a {@link DiscordUser} for a specific {@link Anime}.
     *
     * @param anime
     *         The {@link Anime} for which the {@link InterestLevel} should be set.
     * @param user
     *         The {@link DiscordUser} to which the {@link InterestLevel} belongs.
     * @param level
     *         The {@link InterestLevel} of the {@link DiscordUser} for an {@link Anime}.
     *
     * @return The updated/created {@link Interest} entity.
     */
    public Interest setInterestLevel(Anime anime, DiscordUser user, InterestLevel level) {

        LOGGER.info("setInterestLevel: Interest on Anime {} for User {} is {}", anime.getId(), user.getId(), level.name());

        Interest interest = this.repository.findByAnimeAndUser(anime, user)
                                           .orElseGet(() -> new Interest(anime, user, level));

        interest.setLevel(level);

        Interest saved = this.repository.save(interest);
        LOGGER.debug("Sending InterestUpdatedEvent...");
        this.publisher.publishEvent(new InterestUpdatedEvent(this, saved));

        return saved;
    }
}
