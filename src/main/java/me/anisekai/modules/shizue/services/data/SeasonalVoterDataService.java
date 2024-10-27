package me.anisekai.modules.shizue.services.data;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.chiya.services.data.UserDataService;
import me.anisekai.modules.shizue.components.RankingHandler;
import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.entities.SeasonalVoter;
import me.anisekai.modules.shizue.entities.keys.SeasonalVoterKey;
import me.anisekai.modules.shizue.events.seasonalvoter.SeasonalVoterCreatedEvent;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalVoter;
import me.anisekai.modules.shizue.repositories.SeasonalVoterRepository;
import me.anisekai.modules.shizue.services.proxy.SeasonalVoterProxyService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeasonalVoterDataService extends DataService<SeasonalVoter, SeasonalVoterKey, ISeasonalVoter, SeasonalVoterRepository, SeasonalVoterProxyService> {

    private final UserDataService userService;
    private final RankingHandler  ranking;

    public SeasonalVoterDataService(SeasonalVoterProxyService proxy, UserDataService userService, RankingHandler ranking) {

        super(proxy);
        this.userService = userService;
        this.ranking     = ranking;
    }

    public Set<SeasonalVoter> create(SeasonalSelection selection, long votes) {

        Map<DiscordUser, Integer> voteAmount = new HashMap<>();

        List<DiscordUser> voters = this.userService.getActive().stream()
                                                   .sorted(Comparator.comparing(this.ranking::getUserPower).reversed())
                                                   .limit(votes)
                                                   .peek(user -> voteAmount.put(user, 0))
                                                   .toList();

        long totalVotes = Math.min(votes, selection.getAnimes().size());

        while (totalVotes > 0) {
            for (DiscordUser voter : voters) {
                if (totalVotes == 0) {
                    break;
                }
                voteAmount.put(voter, voteAmount.get(voter) + 1);
                totalVotes--;
            }
        }

        return voters.stream()
                     .map(voter -> this.create(selection, voter, voteAmount.get(voter)))
                     .collect(Collectors.toSet());
    }

    public SeasonalVoter create(SeasonalSelection selection, DiscordUser user, int amount) {

        return this.getProxy().create(voter -> {
            voter.setSeasonalSelection(selection);
            voter.setUser(user);
            voter.setAmount(amount);
        }, SeasonalVoterCreatedEvent::new);
    }

}
