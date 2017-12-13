package ru.codingworkshop.gymm.service;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.WorkerThread;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.concurrent.atomic.AtomicLong;

import ru.codingworkshop.gymm.service.event.incoming.AddRestTimeEvent;
import ru.codingworkshop.gymm.service.event.incoming.PauseRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.ResumeRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.StartRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.StopRestEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestFinishedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestPausedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestResumedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestStartedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestStoppedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimeAddedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimerTickEvent;
import ru.codingworkshop.gymm.service.state.RestInPause;
import ru.codingworkshop.gymm.service.state.RestInProgress;
import ru.codingworkshop.gymm.service.state.RestInactive;
import ru.codingworkshop.gymm.service.state.State;
import timber.log.Timber;

/**
 * Created by Радик on 22.09.2017 as part of the Gymm project.
 */

@SuppressWarnings("unused")
public final class RestController extends Handler {
    public static final int START_REST_MSG = 0;
    public static final int PAUSE_REST_MSG = 1;
    public static final int RESUME_REST_MSG = 2;
    public static final int STOP_REST_MSG = 3;
    private static final int ADD_TIME_REST_MSG = 4;

    public static final String REST_MILLISECONDS_KEY = "restMillisecondsKey";

    private final AtomicLong millisecondsLeft = new AtomicLong();
    private CountDownTimer timer;

    private final RestInProgress restInProgress;
    private final RestInactive restInactive;
    private final RestInPause restInPause;

    private State currentState;
    private final EventBus restEventBus;

    public RestController(Looper looper) {
        super(looper);

        restEventBus = new EventBus();
        restEventBus.register(this);

        restInProgress = new RestInProgress(this);
        restInactive = new RestInactive(this);
        restInPause = new RestInPause(this);

        currentState = restInactive;
    }

    @Override
    public void handleMessage(Message msg) {
        long millis;
        switch (msg.what) {
            case ADD_TIME_REST_MSG:
                stopTimer();
                millis = millisecondsLeft.get() + getMillis(msg);
                startTimer(millis);
                restEventBus.post(new RestTimeAddedEvent(millis));
                break;

            case START_REST_MSG:
                millis = getMillis(msg);
                startTimer(millis);
                restEventBus.post(new RestStartedEvent(millis));
                break;

            case PAUSE_REST_MSG:
                stopTimer();
                restEventBus.post(new RestPausedEvent());
                break;

            case RESUME_REST_MSG:
                startTimer(millisecondsLeft.get());
                restEventBus.post(new RestResumedEvent(millisecondsLeft.get()));
                break;

            case STOP_REST_MSG:
                stopTimer();
                millisecondsLeft.set(0L);
                restEventBus.post(new RestStoppedEvent());
                break;

            default:
                throw new IllegalArgumentException("Wrong timer message type");
        }
    }

    private long getMillis(Message msg) {
        final Bundle data = msg.getData();
        Preconditions.checkArgument(data.containsKey(REST_MILLISECONDS_KEY), "Message object must contain rest time");
        return data.getLong(REST_MILLISECONDS_KEY);
    }

    @WorkerThread
    private void startTimer(long milliseconds) {
        Timber.d("startTimer: %d", milliseconds);

        millisecondsLeft.set(milliseconds);

        timer = new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisecondsLeft.set(millisUntilFinished);
                restEventBus.post(new RestTimerTickEvent(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                setState(restInactive);
                restEventBus.post(new RestFinishedEvent());
            }
        };

        timer.start();
    }

    @WorkerThread
    private void stopTimer() {
        Timber.d("stopTimer");

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    EventBus getRestEventBus() {
        return restEventBus;
    }

    @Subscribe
    public void startRest(StartRestEvent event) {
        getState().startRest(event.getMilliseconds());
    }

    @Subscribe
    public void pauseRest(PauseRestEvent event) {
        getState().pauseRest();
    }

    @Subscribe
    public void resumeRest(ResumeRestEvent event) {
        getState().resumeRest();
    }

    @Subscribe
    public void finishRest(StopRestEvent event) {
        getState().stopRest();
    }

    @Subscribe
    public void addRestTime(AddRestTimeEvent event) {
        obtainRestMessageWithTime(ADD_TIME_REST_MSG, event.getMilliseconds()).sendToTarget();
    }

    public RestInProgress getRestInProgress() {
        return restInProgress;
    }

    public RestInactive getRestInactive() {
        return restInactive;
    }

    public RestInPause getRestInPause() {
        return restInPause;
    }

    private synchronized State getState() {
        return currentState;
    }

    public synchronized void setState(State state) {
        this.currentState = state;
    }

    public long getMillisecondsLeft() {
        return millisecondsLeft.get();
    }

    public Message obtainRestStartMessage(long restTimeInMillis) {
        return obtainRestMessageWithTime(START_REST_MSG, restTimeInMillis);
    }

    public Message obtainRestMessageWithTime(int what, long restTimeInMillis) {
        Message message = obtainMessage(what);
        final Bundle data = new Bundle(1);
        data.putLong(REST_MILLISECONDS_KEY, restTimeInMillis);
        message.setData(data);
        return message;
    }

    public boolean isRestInProgress() {
        return getState() instanceof RestInProgress;
    }

    public boolean isRestInPause() {
        return getState() instanceof RestInPause;
    }
}
