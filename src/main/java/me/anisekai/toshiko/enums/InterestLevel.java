package me.anisekai.toshiko.enums;

import me.anisekai.toshiko.modules.discord.Texts;

public enum InterestLevel {

    INTERESTED(1, Texts.ANIME_INTEREST__OPTION_LEVEL__CHOICE_INTERESTED),
    NEUTRAL(0, Texts.ANIME_INTEREST__OPTION_LEVEL__CHOICE_NEUTRAL),
    NOT_INTERESTED(-1, Texts.ANIME_INTEREST__OPTION_LEVEL__CHOICE_NOT_INTERESTED);

    private final int    powerModifier;
    private final String displayText;

    InterestLevel(int powerModifier, String displayText) {

        this.powerModifier = powerModifier;
        this.displayText   = displayText;
    }

    public static InterestLevel from(String value) {

        String upperValue = value.toUpperCase();
        try {
            return InterestLevel.valueOf(upperValue);
        } catch (IllegalArgumentException e) {
            return InterestLevel.NEUTRAL;
        }
    }

    public int getPowerModifier() {

        return this.powerModifier;
    }

    public String getDisplayText() {

        return this.displayText;
    }

    public boolean isNonNeutral() {

        return this != NEUTRAL;
    }
}
