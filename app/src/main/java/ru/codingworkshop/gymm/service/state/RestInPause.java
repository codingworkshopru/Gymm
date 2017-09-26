package ru.codingworkshop.gymm.service.state;

import ru.codingworkshop.gymm.service.RestController;
import timber.log.Timber;

import static ru.codingworkshop.gymm.service.RestController.RESUME_REST_MSG;
import static ru.codingworkshop.gymm.service.RestController.STOP_REST_MSG;

/**
 * Created by Радик on 22.09.2017 as part of the Gymm project.
 */

public class RestInPause implements State {
    private RestController context;

    public RestInPause(RestController context) {
        this.context = context;
    }

    @Override
    public void startRest(long milliseconds) {
        Timber.w("Rest in progress already");
    }

    @Override
    public void pauseRest() {
        Timber.w("Rest is paused already");
    }

    @Override
    public void resumeRest() {
        context.obtainMessage(RESUME_REST_MSG).sendToTarget();
        context.setState(context.getRestInProgress());
    }

    @Override
    public void stopRest() {
        context.obtainMessage(STOP_REST_MSG).sendToTarget();
        context.setState(context.getRestInactive());
    }
}
