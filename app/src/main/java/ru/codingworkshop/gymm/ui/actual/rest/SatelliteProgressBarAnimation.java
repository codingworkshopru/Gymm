package ru.codingworkshop.gymm.ui.actual.rest;

import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;

/**
 * Created by Радик on 27.09.2017 as part of the Gymm project.
 */

class SatelliteProgressBarAnimation {
    private ObjectAnimator progressBarAnimator;

    SatelliteProgressBarAnimation(SatelliteProgressBar progressBar, long duration) {
        progressBarAnimator = ObjectAnimator.ofFloat(progressBar, "angle", 0f, 360f);
        setDuration(duration);
        progressBarAnimator.setInterpolator(new LinearInterpolator());
    }

    void setDuration(long millis) {
        progressBarAnimator.setDuration(millis);
    }

    void start() {
        progressBarAnimator.setupStartValues();
        progressBarAnimator.start();
    }

    void pause() {
        progressBarAnimator.cancel();
    }

    void stop() {
        progressBarAnimator.end();
    }
}