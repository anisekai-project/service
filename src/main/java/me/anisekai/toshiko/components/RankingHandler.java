package me.anisekai.toshiko.components;

import jakarta.annotation.PostConstruct;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.events.anime.AnimeStatusUpdatedEvent;
import me.anisekai.toshiko.events.interest.InterestLevelUpdatedEvent;
import me.anisekai.toshiko.events.user.UserActiveUpdatedEvent;
import me.anisekai.toshiko.interfaces.entities.IInterest;
import me.anisekai.toshiko.repositories.InterestRepository;
import me.anisekai.toshiko.utils.MapUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RankingHandler {

    private final InterestRepository interestRepository;

    private final Map<DiscordUser, Double> userPower;
    private final Map<Anime, Double>       animeScore;

    public RankingHandler(InterestRepository interestRepository) {

        this.interestRepository = interestRepository;

        this.userPower  = new HashMap<>();
        this.animeScore = new HashMap<>();
    }

    public double getUserPower(DiscordUser user) {

        return this.userPower.getOrDefault(user, 0d);
    }

    public double getAnimeScore(Anime anime) {

        return this.animeScore.getOrDefault(anime, 0d);
    }

    public Map<DiscordUser, Double> getUserPowerMap() {

        return this.userPower;
    }

    public Map<Anime, Double> getAnimeScore() {

        return this.animeScore;
    }

    @PostConstruct
    private void process() {

        this.userPower.clear();
        this.animeScore.clear();

        List<Interest> interests = this.interestRepository
                .findAllActive()
                .stream()
                .filter(interest -> interest.getLevel().isNonNeutral())
                .filter(interest -> interest.getAnime().getStatus().isWatchable())
                .toList();

        List<DiscordUser> users = interests
                .stream()
                .map(Interest::getUser)
                .distinct()
                .toList();

        long votes = interests.size();

        for (DiscordUser user : users) {
            long userVotes = interests.stream().filter(vote -> vote.getUser().equals(user)).count();
            this.userPower.put(user, (double) userVotes / (double) votes);
        }

        MapUtils.groupBy(interests, Interest::getAnime).forEach((anime, animeInterests) -> {
            double score = animeInterests.stream()
                                         .mapToDouble(this::getInterestPower)
                                         .sum();

            this.animeScore.put(anime, score);
        });
    }

    private double getInterestPower(IInterest interest) {

        return this.userPower.getOrDefault(interest.getUser(), 0.0) * interest.getLevel().getPowerModifier() * 100;
    }

    @EventListener({
            AnimeStatusUpdatedEvent.class,
            InterestLevelUpdatedEvent.class,
            UserActiveUpdatedEvent.class
    })
    public void onAnimeStatusUpdated() {

        this.process();
    }

}
