package fr.anisekai.server.services;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.entities.Selection;
import fr.anisekai.server.entities.Voter;
import fr.anisekai.server.entities.adapters.VoterEventAdapter;
import fr.anisekai.server.exceptions.selection.SelectionAnimeNotFoundException;
import fr.anisekai.server.exceptions.voter.VoterMaxReachedException;
import fr.anisekai.server.persistence.DataService;
import fr.anisekai.server.proxy.VoterProxy;
import fr.anisekai.server.repositories.VoterRepository;
import fr.anisekai.wireless.remote.keys.VoterKey;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoterService extends DataService<Voter, VoterKey, VoterEventAdapter, VoterRepository, VoterProxy> {

    private final UserService userService;

    public VoterService(VoterProxy proxy, UserService userService) {

        super(proxy);
        this.userService = userService;
    }

    public void castVote(Selection selection, DiscordUser user, Anime anime) {

        if (!selection.getAnimes().contains(anime)) {
            throw new SelectionAnimeNotFoundException();
        }

        Voter voter = this.fetch(repo -> repo.findBySelectionAndUser(selection, user));

        if (voter.getVotes().contains(anime)) {
            this.mod(voter.getId(), entity -> entity.getVotes().remove(anime));
            return;
        }

        if (voter.getAmount() == voter.getVotes().size()) {
            throw new VoterMaxReachedException();
        }

        this.mod(voter.getId(), entity -> entity.getVotes().add(anime));
    }

    public List<Voter> getVoters(Selection selection) {

        return this.fetchAll(repo -> repo.findBySelection(selection));
    }

    public List<Voter> createVoters(Selection selection, long maxVote) {

        List<DiscordUser>       activeUsers = this.userService.getActiveUsers();
        Map<DiscordUser, Short> voteMap     = new HashMap<>();
        activeUsers.forEach(user -> voteMap.put(user, (short) 0));

        long voteLeft = maxVote;
        int  i        = 0;
        while (voteLeft > 0) {
            DiscordUser user = activeUsers.get(i);
            voteMap.put(user, (short) (voteMap.get(user) + 1));
            voteLeft -= 1;
            i = activeUsers.size() - 1 == i ? 0 : i + 1;
        }

        return activeUsers.stream()
                          .map(user -> this.createVoter(selection, user, voteMap.get(user)))
                          .toList();
    }

    public Voter createVoter(Selection selection, DiscordUser user, short amount) {

        return this.getProxy().create(voter -> {
            voter.setSelection(selection);
            voter.setAmount(amount);
            voter.setUser(user);
        });
    }

}
