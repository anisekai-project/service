package me.anisekai.server.interfaces;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.server.events.voter.VoterAmountUpdatedEvent;

import java.util.Set;

/**
 * Interface representing an object holding data about a {@link IDiscordUser} being able to vote on a
 * {@link ISelection}.
 */
public interface IVoter<O extends IDiscordUser, T extends ISelection<C>, C extends IAnime<O>> extends IEntity<Long> {

    /**
     * Retrieve the {@link ISelection} onto which this {@link IVoter} can cast votes.
     *
     * @return A {@link ISelection}.
     */
    T getSelection();

    /**
     * Define the {@link ISelection} onto which this {@link IVoter} can cast votes.
     *
     * @param selection
     *         A {@link ISelection}.
     */
    void setSelection(T selection);

    /**
     * Retrieve this {@link IVoter}'s identity.
     *
     * @return A {@link IDiscordUser}.
     */
    O getUser();

    /**
     * Define this {@link IVoter}'s identity.
     *
     * @param user
     *         A {@link IDiscordUser}.
     */
    void setUser(O user);

    /**
     * Retrieve this {@link IVoter} amount of votes they can cast in total.
     *
     * @return A vote amount
     */
    long getAmount();

    /**
     * Define this {@link IVoter} amount of votes they can cast in total.
     *
     * @param amount
     *         A vote amount
     */
    @TriggerEvent(VoterAmountUpdatedEvent.class)
    void setAmount(long amount);

    /**
     * Retrieve this {@link IVoter} votes. Votes are represented by a {@link Set} of their voted {@link IAnime}.
     *
     * @return A {@link Set} of {@link IAnime}.
     */
    Set<C> getVotes();

    /**
     * Define this {@link IVoter} votes. Votes are represented by a {@link Set} of their voted {@link IAnime}.
     *
     * @param votes
     *         A {@link Set} of {@link IAnime}.
     */
    @TriggerEvent(VoterAmountUpdatedEvent.class)
    void setVotes(Set<C> votes);

}
