package me.anisekai.toshiko.services;

import me.anisekai.toshiko.components.RankingHandler;
import me.anisekai.toshiko.entities.*;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.events.selections.SeasonalSelectionClosedEvent;
import me.anisekai.toshiko.exceptions.selections.*;
import me.anisekai.toshiko.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SeasonalSelectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeasonalSelectionService.class);

    private final SeasonalSelectionRepository repository;
    private final RankingHandler              ranking;
    private final ApplicationEventPublisher   publisher;

    private final UserRepository  userRepository;
    private final AnimeRepository animeRepository;

    private final SeasonalVoteRepository  voteRepository;
    private final SeasonalVoterRepository voterRepository;

    public SeasonalSelectionService(SeasonalSelectionRepository repository, RankingHandler ranking, ApplicationEventPublisher publisher, UserRepository userRepository, AnimeRepository animeRepository, SeasonalVoteRepository voteRepository, SeasonalVoterRepository voterRepository) {

        this.repository      = repository;
        this.ranking         = ranking;
        this.publisher       = publisher;
        this.userRepository  = userRepository;
        this.animeRepository = animeRepository;
        this.voteRepository  = voteRepository;
        this.voterRepository = voterRepository;
    }

    public List<SeasonalVote> getVotes(SeasonalSelection seasonalSelection) {

        return this.voteRepository.findAllBySeasonalSelection(seasonalSelection);
    }

    public List<SeasonalVoter> getVoters(SeasonalSelection seasonalSelection) {

        return this.voterRepository.findAllBySeasonalSelection(seasonalSelection);
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
                .findAllByStatusIn(Collections.singleton(AnimeStatus.SIMULCAST_AVAILABLE));

        ss.setAnimes(new HashSet<>(simulcasts));
        ss.setVoters(Collections.emptySet());
        ss.setVotes(Collections.emptySet());


        // Retrieve all potential voters
        List<DiscordUser> votingUsers = this.userRepository.findAllByActiveIsTrue()
                                                           .stream()
                                                           .sorted(Comparator.comparingDouble(this.ranking::getUserPower))
                                                           .limit(7)
                                                           .toList();

        if (votingUsers.isEmpty()) {
            throw new IllegalStateException("Can't create a Seasonal Selection without at least one active member.");
        }

        SeasonalSelection saved = this.repository.save(ss);

        List<SeasonalVoter> seasonalVoters = votingUsers.stream().map(user -> new SeasonalVoter(saved, user)).toList();
        // Process vote amount
        int amount = Math.min(7, simulcasts.size());

        while (amount > 0) {
            for (SeasonalVoter seasonalVoter : seasonalVoters) {
                if (amount > 0) {
                    seasonalVoter.setAmount(seasonalVoter.getAmount() + 1);
                    amount--;
                }
            }
        }

        saved.setVoters(new HashSet<>(this.voterRepository.saveAll(seasonalVoters)));
        return saved;
    }

    public void closeSeasonalSelection(SeasonalSelection seasonalSelection, boolean ignoreMissingVote) {

        List<SeasonalVote>  votes  = this.getVotes(seasonalSelection);
        List<SeasonalVoter> voters = this.getVoters(seasonalSelection);


        if (seasonalSelection.isClosed()) {
            // You can't close an already closed one
            throw new SeasonalSelectionClosedException(seasonalSelection);
        }

        if (!ignoreMissingVote) {
            // Check if every vote has been cast

            int voteRequired = voters.stream().mapToInt(SeasonalVoter::getAmount).sum();

            if (voteRequired > votes.size()) {
                throw new SeasonalSelectionIncompleteException(seasonalSelection);
            }
        }

        seasonalSelection.setClosed(true);
        SeasonalSelection saved = this.repository.save(seasonalSelection);
        this.publisher.publishEvent(new SeasonalSelectionClosedEvent(this, saved));
    }

    public SeasonalSelection castVote(SeasonalSelection seasonalSelection, DiscordUser user, Anime anime) {

        Set<SeasonalVote>  votes  = seasonalSelection.getVotes();
        Set<SeasonalVoter> voters = seasonalSelection.getVoters();

        Optional<SeasonalVoter> optionalVoter = voters
                .stream()
                .filter(sv -> sv.getUser().equals(user))
                .findFirst();

        if (optionalVoter.isEmpty()) {
            throw new UserVotingForbiddenException();
        }

        SeasonalVoter voter = optionalVoter.get();

        // Check if anime is already voted.
        Optional<SeasonalVote> optionalVote = votes
                .stream()
                .filter(sv -> sv.getAnime().equals(anime))
                .findFirst();

        if (optionalVote.isPresent()) {
            // Sanity checks

            SeasonalVote vote = optionalVote.get();

            if (!vote.getUser().equals(user)) {
                throw new AnimeAlreadyVotedException();
            }

            this.voteRepository.delete(vote);
            return this.getSelection(seasonalSelection.getId());
        }

        long count = votes.stream().filter(sv -> sv.getUser().equals(user)).count();

        if (voter.getAmount() <= count) {
            throw new TooMuchVoteException();
        }

        this.voteRepository.save(new SeasonalVote(seasonalSelection, user, anime));
        return this.getSelection(seasonalSelection.getId());
    }
}
