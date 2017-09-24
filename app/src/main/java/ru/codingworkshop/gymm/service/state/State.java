package ru.codingworkshop.gymm.service.state;

/**
 * Created by Радик on 22.09.2017 as part of the Gymm project.
 */

public interface State {
    void startRest(long milliseconds);
    void pauseRest();
    void resumeRest();
    void stopRest();
}
