package ru.codingworkshop.gymm.ui.actual.rest;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.view.animation.LinearInterpolator;

/**
 * Created by Радик on 27.09.2017 as part of the Gymm project.
 */

@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
public class SatelliteProgressBarAnimation {
    private ObjectAnimator progressBarAnimator;

    SatelliteProgressBarAnimation(SatelliteProgressBar progressBar, long duration) {
        progressBarAnimator = ObjectAnimator.ofFloat(progressBar, "angle", 0f, 360f);
        setDuration(duration);
        progressBarAnimator.setInterpolator(new LinearInterpolator());
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public void setDuration(long millis) {
        progressBarAnimator.setDuration(millis);
    }

    void start() {
        progressBarAnimator.setupStartValues();
        progressBarAnimator.start();
    }

    void pause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            progressBarAnimator.pause();
        } else {
            long currentPlayTime = progressBarAnimator.getCurrentPlayTime();
            progressBarAnimator.cancel();
            setDuration(progressBarAnimator.getDuration() - currentPlayTime);
        }
    }

    void stop() {
        progressBarAnimator.end();
    }
}
