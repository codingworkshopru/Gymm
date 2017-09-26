package ru.codingworkshop.gymm.service.event.outcoming;

import ru.codingworkshop.gymm.service.event.MillisecondsEvent;

/**
 * Created by Радик on 26.09.2017 as part of the Gymm project.
 */

public class RestTimeAddedEvent extends MillisecondsEvent {
    public RestTimeAddedEvent(long milliseconds) {
        super(milliseconds);
    }
}
