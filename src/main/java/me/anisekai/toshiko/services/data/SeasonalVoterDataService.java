package me.anisekai.toshiko.services.data;

import me.anisekai.toshiko.components.RankingHandler;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.entities.SeasonalVoter;
import me.anisekai.toshiko.entities.keys.SeasonalVoterKey;
import me.anisekai.toshiko.events.seasonalvoter.SeasonalVoterCreatedEvent;
import me.anisekai.toshiko.interfaces.entities.ISeasonalVoter;
import me.anisekai.toshiko.repositories.SeasonalVoterRepository;
import me.anisekai.toshiko.services.AbstractDataService;
import me.anisekai.toshiko.services.proxy.SeasonalVoterProxyService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeasonalVoterDataService extends AbstractDataService<SeasonalVoter, SeasonalVoterKey, ISeasonalVoter, SeasonalVoterRepository, SeasonalVoterProxyService> {

    private final UserDataService userService;
    private final RankingHandler  ranking;

    public SeasonalVoterDataService(SeasonalVoterProxyService proxy, UserDataService userService, RankingHandler ranking) {

        super(proxy);
        this.userService = userService;
        this.ranking     = ranking;
    }

    public Set<SeasonalVoter> create(SeasonalSelection selection) {

        Map<DiscordUser, Integer> voteAmount = new HashMap<>(7);

        List<DiscordUser> voters = this.userService.getActive().stream()
                                                   .sorted(Comparator.comparing(this.ranking::getUserPower).reversed())
                                                   .limit(7)
                                                   .peek(user -> voteAmount.put(user, 0))
                                                   .toList();

        int totalVotes = Math.min(7, selection.getAnimes().size());

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
