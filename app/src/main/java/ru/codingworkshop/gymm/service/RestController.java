package ru.codingworkshop.gymm.service;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.WorkerThread;

import com.google.common.base.Preconditions;

import java.util.concurrent.atomic.AtomicLong;

import ru.codingworkshop.gymm.service.state.RestInPause;
import ru.codingworkshop.gymm.service.state.RestInProgress;
import ru.codingworkshop.gymm.service.state.RestInactive;
import ru.codingworkshop.gymm.service.state.State;
import timber.log.Timber;

/**
 * Created by Радик on 22.09.2017 as part of the Gymm project.
 */
public final class RestController extends Handler {
    public static final int START_REST_MSG = 0;
    public static final int PAUSE_REST_MSG = 1;
    public static final int STOP_REST_MSG = 2;
    private static final int ADD_TIME_REST_MSG = 4;

    public static final String REST_MILLISECONDS_KEY = "restMillisecondsKey";

    private AtomicLong millisecondsLeft = new AtomicLong();
    private CountDownTimer timer;

    private RestInProgress restInProgress;
    private RestInactive restInactive;
    private RestInPause restInPause;

    private State currentState;

    public RestController(Looper looper) {
        super(looper);
        restInProgress = new RestInProgress(this);
        restInactive = new RestInactive(this);
        restInPause = new RestInPause(this);

        currentState = restInactive;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {

            case ADD_TIME_REST_MSG:
                stopTimer();

            case START_REST_MSG:
                final Bundle data = msg.getData();
                Preconditions.checkArgument(data.containsKey(REST_MILLISECONDS_KEY), "Message object must contain rest time");
                long millis = data.getLong(REST_MILLISECONDS_KEY);
                startTimer(millis);
                break;

            case PAUSE_REST_MSG:
                stopTimer();
                break;

            case STOP_REST_MSG:
                stopTimer();
                millisecondsLeft.set(0L);
                break;

            default:
                throw new IllegalArgumentException("Wrong timer message type");
        }
    }

    @WorkerThread
    private void startTimer(long milliseconds) {
        Timber.d("startTimer: %d", milliseconds);

        millisecondsLeft.set(milliseconds);

        timer = new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisecondsLeft.set(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                stopRest();
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

    public void startRest(long milliseconds) {
        getState().startRest(milliseconds);
    }

    public void pauseRest() {
        getState().pauseRest();
    }

    public void resumeRest() {
        getState().resumeRest();
    }

    public void stopRest() {
        getState().stopRest();
    }

    public void addRestTime(long additionalTime) {
        final long totalTime = getMillisecondsLeft() + additionalTime;
        obtainRestMessageWithTime(ADD_TIME_REST_MSG, totalTime).sendToTarget();
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
