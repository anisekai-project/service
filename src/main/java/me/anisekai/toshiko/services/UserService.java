package me.anisekai.toshiko.services;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.exceptions.users.EmojiAlreadyUsedException;
import me.anisekai.toshiko.exceptions.users.InvalidEmojiException;
import me.anisekai.toshiko.repositories.InterestRepository;
import me.anisekai.toshiko.repositories.UserRepository;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository     repository;
    private final InterestRepository interestRepository;

    public UserService(UserRepository repository, InterestRepository interestRepository) {

        this.repository         = repository;
        this.interestRepository = interestRepository;
    }

    public DiscordUser retrieve(User user) {

        Optional<DiscordUser> optionalDiscordUser = this.repository.findById(user.getIdLong());
        DiscordUser           discordUser         = optionalDiscordUser.orElseGet(() -> new DiscordUser(user));
        return this.repository.save(discordUser);
    }

    public boolean swapEmoji(User user, String emoji) {

        if (emoji.matches("\\w*")) {
            throw new InvalidEmojiException();
        }

        DiscordUser discordUser = this.retrieve(user);

        if (emoji.equalsIgnoreCase(discordUser.getEmote())) {
            return false;
        }

        if (this.repository.findAll().stream().anyMatch(otherUser -> emoji.equals(otherUser.getEmote()))) {
            throw new EmojiAlreadyUsedException();
        }

        discordUser.setEmote(emoji);
        this.repository.save(discordUser);
        return true;
    }

    public Map<DiscordUser, Double> getVotePercentage() {

        Map<DiscordUser, Double> power = new HashMap<>();

        List<Interest> interests = this.interestRepository.findAll()
                                                          .stream()
                                                          .filter(vote -> vote.getLevel() != InterestLevel.NEUTRAL)
                                                          .toList();

        List<DiscordUser> users = interests.stream()
                                           .map(Interest::getUser)
                                           .distinct()
                                           .toList();


        long nonNeutralVote = interests.size();

        for (DiscordUser user : users) {
            long nonNeutralUserVote = interests.stream()
                                               .filter(vote -> vote.getUser().equals(user))
                                               .count();

            power.put(user, (double) nonNeutralUserVote / (double) nonNeutralVote);
        }

        return power;
    }
}
