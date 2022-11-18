package me.anisekai.toshiko.utils;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.helpers.comparators.AnimeScoreComparator;
import me.anisekai.toshiko.helpers.containers.VariablePair;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class DiscordUtils {

    private DiscordUtils() {}

    public static String getTopDescFormatted(Map<Anime, Double> votes, AnimeStatus status, int count) {

        return getTopFormatted(votes, status, count, true);
    }

    public static String getTopAscFormatted(Map<Anime, Double> votes, AnimeStatus status, int count) {

        return getTopFormatted(votes, status, count, false);
    }

    public static String getTopFormatted(Map<Anime, Double> votes, AnimeStatus status, int count, boolean reverse) {

        return votes.entrySet().stream()
                    .filter(entry -> entry.getKey().getStatus() == status)
                    .sorted(new AnimeScoreComparator(reverse))
                    .limit(count)
                    .map(entry -> DiscordUtils.buildAnimeList(entry).getFirst())
                    .collect(Collectors.joining("\n\n"));
    }

    public static VariablePair<String, String> buildAnimeList(Anime anime, Double score) {

        Set<Interest> interested = anime.getInterests();

        String interestedUserIcon = interested.stream()
                                              .filter(interest -> interest.getLevel() == InterestLevel.INTERESTED)
                                              .map(Interest::getUser)
                                              .map(DiscordUser::getEmote)
                                              .filter(Objects::nonNull)
                                              .collect(Collectors.joining());

        String notInterestedUserIcon = interested.stream()
                                                 .filter(interest -> interest.getLevel() == InterestLevel.NOT_INTERESTED)
                                                 .map(Interest::getUser)
                                                 .map(DiscordUser::getEmote)
                                                 .filter(Objects::nonNull)
                                                 .collect(Collectors.joining());

        boolean hasUpVotes   = !interestedUserIcon.isEmpty();
        boolean hasDownVotes = !notInterestedUserIcon.isEmpty();
        boolean hasProgress  = anime.getStatus() == AnimeStatus.WATCHING || anime.getStatus() == AnimeStatus.SIMULCAST;

        StringBuilder entryWithLinkBuilder    = new StringBuilder();
        StringBuilder entryWithoutLinkBuilder = new StringBuilder();

        String linkEntryScore = "[%s](%s) ─ %.2f";
        String linkEntry      = "[%s](%s)";
        String upVoteEntry    = "▲ %s";
        String downVoteEntry  = "▼ %s";
        String progressEntry  = "%s/%s";

        if (score > 0) {
            entryWithLinkBuilder.append(linkEntryScore.formatted(anime.getName(), anime.getLink(), score));
        } else {
            entryWithLinkBuilder.append(linkEntry.formatted(anime.getName(), anime.getLink(), score));
        }

        entryWithoutLinkBuilder.append(anime.getName());

        if (hasProgress) {
            if (anime.getTotal() > 0) {
                entryWithLinkBuilder.append(" ─ ")
                                    .append(progressEntry.formatted(anime.getWatched(), anime.getTotal()));
                entryWithoutLinkBuilder.append(" ─ ")
                                       .append(progressEntry.formatted(anime.getWatched(), anime.getTotal()));
            } else {
                entryWithLinkBuilder.append(" ─ ").append(progressEntry.formatted(anime.getWatched(), "?"));
                entryWithoutLinkBuilder.append(" ─ ").append(progressEntry.formatted(anime.getWatched(), "?"));
            }
        }

        if (!hasUpVotes && !hasDownVotes) {
            entryWithLinkBuilder.append("\n> *Pas de vote*");
            entryWithoutLinkBuilder.append("\n> *Pas de vote*");
        } else if (hasUpVotes && hasDownVotes) {
            entryWithLinkBuilder.append("\n> ")
                                .append(upVoteEntry.formatted(interestedUserIcon))
                                .append(" ─ ")
                                .append(downVoteEntry.formatted(notInterestedUserIcon));
            entryWithoutLinkBuilder.append("\n> ")
                                   .append(upVoteEntry.formatted(interestedUserIcon))
                                   .append(" ─ ")
                                   .append(downVoteEntry.formatted(notInterestedUserIcon));
        } else if (hasUpVotes) {
            entryWithLinkBuilder.append("\n> ")
                                .append(upVoteEntry.formatted(interestedUserIcon));
            entryWithoutLinkBuilder.append("\n> ")
                                   .append(upVoteEntry.formatted(interestedUserIcon));
        } else {
            entryWithLinkBuilder.append("\n> ")
                                .append(downVoteEntry.formatted(notInterestedUserIcon));
            entryWithoutLinkBuilder.append("\n> ")
                                   .append(downVoteEntry.formatted(notInterestedUserIcon));
        }


        return new VariablePair<>(entryWithLinkBuilder.toString(), entryWithoutLinkBuilder.toString());
    }

    public static VariablePair<String, String> buildAnimeList(Map.Entry<Anime, Double> data) {

        return buildAnimeList(data.getKey(), data.getValue());
    }

    public static long getNearest(long value, int mod) {

        long fv = value;
        while (fv % mod > 0) {
            fv++;
        }
        return fv;
    }
}
