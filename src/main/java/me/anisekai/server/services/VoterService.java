package me.anisekai.server.services;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.entities.Selection;
import me.anisekai.server.entities.Voter;
import me.anisekai.server.exceptions.selection.SelectionAnimeNotFoundException;
import me.anisekai.server.exceptions.voter.VoterMaxReachedException;
import me.anisekai.server.interfaces.IVoter;
import me.anisekai.server.proxy.VoterProxy;
import me.anisekai.server.repositories.VoterRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoterService extends DataService<Voter, Long, IVoter<DiscordUser, Selection, Anime>, VoterRepository, VoterProxy> {

    private final DiscordUserService userService;

    public VoterService(VoterProxy proxy, DiscordUserService userService) {

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

        List<DiscordUser>         activeUsers = this.userService.getActiveUsers();
        Map<DiscordUser, Integer> voteMap     = new HashMap<>();
        activeUsers.forEach(user -> voteMap.put(user, 0));

        long voteLeft = maxVote;
        int  i        = 0;
        while (voteLeft > 0) {
            DiscordUser user = activeUsers.get(i);
            voteMap.put(user, voteMap.get(user) + 1);
            voteLeft -= 1;
            i = activeUsers.size() - 1 == i ? 0 : i + 1;
        }

        return activeUsers.stream()
                          .map(user -> this.createVoter(selection, user, voteMap.get(user)))
                          .toList();
    }

    public Voter createVoter(Selection selection, DiscordUser user, long amount) {

        return this.getProxy().create(voter -> {
            voter.setSelection(selection);
            voter.setAmount(amount);
            voter.setUser(user);
        });
    }

}
