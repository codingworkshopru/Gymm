package ru.codingworkshop.gymm.service.state;

import ru.codingworkshop.gymm.service.RestController;
import timber.log.Timber;

/**
 * Created by Радик on 22.09.2017 as part of the Gymm project.
 */

public class RestInactive implements State {
    private RestController context;

    public RestInactive(RestController context) {
        this.context = context;
    }

    @Override
    public void startRest(long milliseconds) {
        if (milliseconds <= 0) return;

        context.obtainRestStartMessage(milliseconds).sendToTarget();
        context.setState(context.getRestInProgress());
    }

    @Override
    public void pauseRest() {
        Timber.w("Cannot pause inactive rest");
    }

    @Override
    public void resumeRest() {
        Timber.w("Cannot result inactive rest");
    }

    @Override
    public void stopRest() {
        Timber.w("Cannot stop inactive rest");
    }
}
