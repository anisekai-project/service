package me.anisekai.toshiko.messages.embeds.watchlist;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.enums.InterestLevel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WatchlistAnimePart {

    private final Anime             anime;
    private final double            score;
    private final List<DiscordUser> positiveVotes;
    private final List<DiscordUser> negativeVotes;

    public WatchlistAnimePart(Anime anime, double score) {

        this.anime = anime;
        this.score = score;

        this.positiveVotes = anime.getInterests()
                                  .stream()
                                  .filter(interest -> interest.getLevel() == InterestLevel.INTERESTED)
                                  .map(Interest::getUser)
                                  .sorted(Comparator.comparing(DiscordUser::getId))
                                  .toList();

        this.negativeVotes = anime.getInterests()
                                  .stream()
                                  .filter(interest -> interest.getLevel() == InterestLevel.NOT_INTERESTED)
                                  .map(Interest::getUser)
                                  .sorted(Comparator.comparing(DiscordUser::getId))
                                  .toList();
    }

    private String getTitleLinkScore() {

        return String.format("[%s](%s) ─ %.2f", this.anime.getName(), this.anime.getLink(), this.score);
    }

    private String getTitleLink() {

        return String.format("[%s](%s)", this.anime.getName(), this.anime.getLink());
    }

    private String getTitleLinkProgress() {

        return String.format(
                "[%s](%s) ─ %02d/%02d",
                this.anime.getName(),
                this.anime.getLink(),
                this.anime.getWatched(),
                this.anime.getTotal()
        );
    }

    private String getTitleProgress() {

        return String.format(
                "**%s** ─ %02d/%02d",
                this.anime.getName(),
                this.anime.getWatched(),
                this.anime.getTotal()
        );
    }

    private String getTitleScore() {

        return String.format("**%s** ─ %.2f", this.anime.getName(), this.score);
    }

    private String getTitle() {

        return String.format("**%s**", this.anime.getName());
    }

    private String getPositiveVotes() {

        return String.format("▲ %s", this.positiveVotes.stream()
                                                       .map(DiscordUser::getEmote)
                                                       .filter(Objects::nonNull)
                                                       .collect(Collectors.joining()));
    }

    private String getNegativeVotes() {

        return String.format("▼ %s", this.negativeVotes.stream()
                                                       .map(DiscordUser::getEmote)
                                                       .filter(Objects::nonNull)
                                                       .collect(Collectors.joining()));
    }

    private String getFull(boolean enableLink) {

        String lineFormat = "%s\n> %s\n";

        String title = switch (this.anime.getStatus()) {
            case WATCHING, SIMULCAST -> enableLink ? this.getTitleLinkProgress() : this.getTitleProgress();
            case SIMULCAST_AVAILABLE, DOWNLOADED -> enableLink ? this.getTitleLinkScore() : this.getTitleScore();
            default -> enableLink ? this.getTitleLink() : this.getTitle();
        };

        boolean hasPositiveVote = !this.positiveVotes.isEmpty();
        boolean hasNegativeVote = !this.negativeVotes.isEmpty();

        if (hasPositiveVote && hasNegativeVote) {
            return String.format(
                    lineFormat,
                    title,
                    String.join(" ─ ", Arrays.asList(this.getPositiveVotes(), this.getNegativeVotes()))
            );
        }

        if (hasPositiveVote) {
            return String.format(lineFormat, title, this.getPositiveVotes());
        }

        if (hasNegativeVote) {
            return String.format(lineFormat, title, this.getNegativeVotes());
        }

        return String.format(lineFormat, title, "*Aucun vote*");
    }

    private String getMacro() {

        return switch (this.anime.getStatus()) {
            case WATCHING, SIMULCAST -> this.getTitleProgress();
            case SIMULCAST_AVAILABLE, DOWNLOADED -> this.getTitleScore();
            default -> this.getTitle();
        };
    }

    public String getFullFormat() {

        return this.getFull(true);
    }

    public String getSmallFormat() {

        return this.getFull(false);
    }

    public String getMacroFormat() {

        return this.getMacro();
    }

}
