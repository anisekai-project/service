package me.anisekai.toshiko.services;

import me.anisekai.toshiko.components.RankingHandler;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.entities.SeasonalVoter;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.events.selections.SeasonalSelectionClosedEvent;
import me.anisekai.toshiko.exceptions.selections.SeasonalSelectionClosedException;
import me.anisekai.toshiko.exceptions.selections.SeasonalSelectionIncompleteException;
import me.anisekai.toshiko.exceptions.selections.SeasonalSelectionNotFoundException;
import me.anisekai.toshiko.repositories.AnimeRepository;
import me.anisekai.toshiko.repositories.SeasonalSelectionRepository;
import me.anisekai.toshiko.repositories.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SeasonalSelectionService {

    private final SeasonalSelectionRepository repository;
    private final RankingHandler              ranking;
    private final ApplicationEventPublisher   publisher;

    private final UserRepository  userRepository;
    private final AnimeRepository animeRepository;

    public SeasonalSelectionService(SeasonalSelectionRepository repository, RankingHandler ranking, ApplicationEventPublisher publisher, UserRepository userRepository, AnimeRepository animeRepository) {

        this.repository      = repository;
        this.ranking         = ranking;
        this.publisher       = publisher;
        this.userRepository  = userRepository;
        this.animeRepository = animeRepository;
    }

    public SeasonalSelection getSelection(long id) {

        return this.repository.findById(id).orElseThrow(SeasonalSelectionNotFoundException::new);
    }

    public SeasonalSelection createSeasonalSelection(String name) {

        SeasonalSelection ss = new SeasonalSelection();
        ss.setId(System.currentTimeMillis());
        ss.setName(name);

        // Get all animes in SIMULCAST state...

        List<Anime> simulcasts = this.animeRepository
                .findAllByStatusIn(Collections.singleton(AnimeStatus.SIMULCAST));

        ss.setAnimes(new HashSet<>(simulcasts));


        // Retrieve all potential voters
        List<SeasonalVoter> seasonalVoters = this.userRepository.findAllByActiveIsTrue()
                                                                .stream()
                                                                .sorted(Comparator.comparingDouble(this.ranking::getUserPower))
                                                                .limit(7)
                                                                .map(user -> new SeasonalVoter(ss, user))
                                                                .toList();

        if (seasonalVoters.isEmpty()) {
            throw new IllegalStateException("Can't create a Seasonal Selection without at least one active member.");
        }

        // Process vote amount
        int amount = 7;

        while (amount > 0) {
            Iterator<SeasonalVoter> iterator = seasonalVoters.iterator();

            while (iterator.hasNext() && amount > 0) {
                SeasonalVoter next = iterator.next();
                next.setAmount(next.getAmount() + 1);
                amount--;
            }
        }

        ss.setVoters(new HashSet<>(seasonalVoters));
        ss.setVotes(Collections.emptySet());

        return this.repository.save(ss);
    }

    public void closeSeasonalSelection(SeasonalSelection seasonalSelection, boolean ignoreMissingVote) {

        if (seasonalSelection.isClosed()) {
            // You can't close an already closed one
            throw new SeasonalSelectionClosedException(seasonalSelection);
        }

        if (!ignoreMissingVote) {
            // Check if every vote has been cast

            int voteRequired = seasonalSelection.getVoters().stream().mapToInt(SeasonalVoter::getAmount).sum();
            int votes        = seasonalSelection.getVotes().size();

            if (voteRequired > votes) {
                throw new SeasonalSelectionIncompleteException(seasonalSelection);
            }
        }

        seasonalSelection.setClosed(true);
        SeasonalSelection saved = this.repository.save(seasonalSelection);
        this.publisher.publishEvent(new SeasonalSelectionClosedEvent(this, saved));
    }
}
