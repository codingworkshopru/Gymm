package ru.codingworkshop.gymm.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by Радик on 11.05.2017.
 */
class TimeHandler extends Handler {
    private final String TAG = TimeHandler.class.getSimpleName();
    static final int MSG_START_REST_COUNTDOWN = 0;

    TimeHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MSG_START_REST_COUNTDOWN) {
            RestTimer timer = ((RestTimer) msg.obj);
            timer.start();
        }
    }
}
