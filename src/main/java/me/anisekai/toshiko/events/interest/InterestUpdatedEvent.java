package me.anisekai.toshiko.events.interest;

import me.anisekai.toshiko.entities.Interest;
import org.springframework.context.ApplicationEvent;

public class InterestUpdatedEvent extends ApplicationEvent {

    private final Interest interest;

    public InterestUpdatedEvent(Object source, Interest interest) {

        super(source);
        this.interest = interest;
    }

    public Interest getInterest() {

        return this.interest;
    }

}
