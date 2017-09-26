package ru.codingworkshop.gymm.service.event.incoming;

import ru.codingworkshop.gymm.service.event.MillisecondsEvent;

/**
 * Created by Радик on 26.09.2017 as part of the Gymm project.
 */

public class AddRestTimeEvent extends MillisecondsEvent {
    public AddRestTimeEvent(long milliseconds) {
        super(milliseconds);
    }
}
