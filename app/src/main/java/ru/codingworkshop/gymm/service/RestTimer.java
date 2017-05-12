package ru.codingworkshop.gymm.service;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;

import com.google.common.eventbus.EventBus;

/**
 * Created by Радик on 11.05.2017.
 */
public class RestTimer extends CountDownTimer {
    private static final int TICK_INTERVAL = 1000;

    private EventBus eventBus;

    RestTimer(long millisInFuture, @NonNull EventBus eventBus) {
        super(millisInFuture, TICK_INTERVAL);
        this.eventBus = eventBus;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        eventBus.post(new TickEvent(millisUntilFinished));
    }

    @Override
    public void onFinish() {
        eventBus.post(new FinishEvent());
    }

    public static final class TickEvent {
        public long millisUntilFinished;

        TickEvent(long millisUntilFinished) {
            this.millisUntilFinished = millisUntilFinished;
        }
    }

    public static final class FinishEvent {
    }
}
