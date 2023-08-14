package me.anisekai.toshiko.services.data;

import me.anisekai.toshiko.entities.*;
import me.anisekai.toshiko.entities.keys.SeasonalVoterKey;
import me.anisekai.toshiko.entities.keys.UserAnimeVoteAssocKey;
import me.anisekai.toshiko.exceptions.seasonalvote.AnimeAlreadyVotedException;
import me.anisekai.toshiko.exceptions.seasonalvote.TooMuchVoteException;
import me.anisekai.toshiko.exceptions.seasonalvote.UserVotingForbiddenException;
import me.anisekai.toshiko.interfaces.entities.ISeasonalVote;
import me.anisekai.toshiko.repositories.SeasonalVoteRepository;
import me.anisekai.toshiko.services.AbstractDataService;
import me.anisekai.toshiko.services.proxy.SeasonalVoteProxyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeasonalVoteDataService extends AbstractDataService<SeasonalVote, UserAnimeVoteAssocKey, ISeasonalVote, SeasonalVoteRepository, SeasonalVoteProxyService> {

    private final SeasonalVoterDataService voterService;

    public SeasonalVoteDataService(SeasonalVoteProxyService proxy, SeasonalVoterDataService voterService) {

        super(proxy);
        this.voterService = voterService;
    }

    public void toggleVote(SeasonalSelection selection, DiscordUser user, Anime anime) {

        // Get all votes
        List<SeasonalVote> votes = this.fetchAll(repository -> repository.findAllBySeasonalSelection(selection));
        List<SeasonalVoter> voters = this.voterService
                .fetchAll(repository -> repository.findAllBySeasonalSelection(selection));

        // Can the user interact ?
        Optional<SeasonalVoter> optionalVoter = voters.stream().filter(voter -> voter.getUser().equals(user)).findAny();

        if (optionalVoter.isEmpty()) {
            throw new UserVotingForbiddenException();
        }

        // Is it already voted ?
        Optional<SeasonalVote> optionalAnimeVote = votes.stream()
                                                        .filter(vote -> vote.getAnime().equals(anime))
                                                        .findAny();

        if (optionalAnimeVote.isPresent()) {
            SeasonalVote animeVote = optionalAnimeVote.get();
            if (!animeVote.getUser().equals(user)) {
                throw new AnimeAlreadyVotedException();
            }
            this.getProxy().getRepository().delete(animeVote);
            return;
        }

        // Check allowance
        List<SeasonalVote> userVotes = votes.stream().filter(vote -> vote.getUser().equals(user)).toList();
        SeasonalVoter voter = this.voterService.fetch(new SeasonalVoterKey(selection, user));

        if (userVotes.size() == voter.getAmount()) {
            throw new TooMuchVoteException();
        }

        this.getProxy().upsert(selection, user, anime, vote -> {});
    }

}
