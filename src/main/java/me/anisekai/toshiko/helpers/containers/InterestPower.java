package me.anisekai.toshiko.helpers.containers;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;

import java.util.List;
import java.util.Map;

public class InterestPower {

    private final Map<DiscordUser, Double> userInterestPower;
    private final List<Interest>           interests;

    public InterestPower(Map<DiscordUser, Double> userInterestPower, List<Interest> interests) {

        this.userInterestPower = userInterestPower;
        this.interests         = interests;
    }

    public List<Interest> getInterests() {

        return this.interests;
    }

    public Map<DiscordUser, Double> getUserInterestPower() {

        return this.userInterestPower;
    }
}
