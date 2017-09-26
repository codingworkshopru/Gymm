package ru.codingworkshop.gymm.service.event;

/**
 * Created by Радик on 26.09.2017 as part of the Gymm project.
 */

public abstract class MillisecondsEvent {
    private long milliseconds;

    public MillisecondsEvent(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public long getMilliseconds() {
        return milliseconds;
    }
}
