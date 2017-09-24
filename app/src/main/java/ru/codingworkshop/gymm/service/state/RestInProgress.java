package ru.codingworkshop.gymm.service.state;

import ru.codingworkshop.gymm.service.RestController;
import timber.log.Timber;

import static ru.codingworkshop.gymm.service.RestController.PAUSE_REST_MSG;
import static ru.codingworkshop.gymm.service.RestController.STOP_REST_MSG;

/**
 * Created by Радик on 22.09.2017 as part of the Gymm project.
 */

public class RestInProgress implements State {
    private RestController context;

    public RestInProgress(RestController context) {
        this.context = context;
    }

    @Override
    public void startRest(long milliseconds) {
        Timber.w("Rest in progress already");
    }

    @Override
    public void pauseRest() {
        context.obtainMessage(PAUSE_REST_MSG).sendToTarget();
        context.setState(context.getRestInPause());
    }

    @Override
    public void resumeRest() {
        Timber.w("Rest in progress already");
    }

    @Override
    public void stopRest() {
        context.obtainMessage(STOP_REST_MSG).sendToTarget();
        context.setState(context.getRestInactive());
    }
}
