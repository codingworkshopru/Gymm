package ru.codingworkshop.gymm.ui.actual.rest;


import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.databinding.FragmentActualTrainingRestBinding;
import ru.codingworkshop.gymm.service.TrainingForegroundService;
import ru.codingworkshop.gymm.service.event.incoming.AddRestTimeEvent;
import ru.codingworkshop.gymm.service.event.incoming.PauseRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.ResumeRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.StartRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.StopRestEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestFinishedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestPausedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestResumedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimeAddedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimerTickEvent;
import ru.codingworkshop.gymm.ui.actual.ServiceBindController;
import timber.log.Timber;

import static android.view.animation.Animation.INFINITE;
import static android.view.animation.Animation.REVERSE;

@SuppressWarnings("unused")
public class ActualTrainingRestFragment extends Fragment {
    private static final String REST_TIME_MILLISECONDS_KEY = "restTimeMillisecondsKey";
    private final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("m:ss", Locale.getDefault());

    public interface ActualTrainingRestCallback {
        void onRestStopped();
    }

    private FragmentActualTrainingRestBinding binding;
    private long totalTimeMilliseconds;
    private EventBus restEventBus;
    private SatelliteProgressBarAnimation progressBarAnimation;
    private Animation fadeInFadeOut;
    private ServiceBindController serviceBindController;

    private ActualTrainingRestCallback callback;

    public enum State {
        PAUSED, IN_PROGRESS, FINISHED
    }

    public void setRestTime(long restTimeMillis) {
        Bundle arguments = new Bundle(1);
        arguments.putLong(REST_TIME_MILLISECONDS_KEY, restTimeMillis);
        setArguments(arguments);
    }

    @Override
    public void onAttach(Context context) {
        Timber.d("onAttach");
        super.onAttach(context);

        if (callback == null) {
            if (context instanceof ActualTrainingRestCallback) {
                callback = (ActualTrainingRestCallback) context;
            } else {
                throw new IllegalStateException("Activity must implement "
                        + ActualTrainingRestCallback.class);
            }
        }

        serviceBindController = new ServiceBindController(getContext());
        if (restEventBus == null) {
            Transformations.map(serviceBindController.bindService(), TrainingForegroundService::getRestEventBus).observe(this, this::onServiceConnected);
        } else {
            onServiceConnected(restEventBus);
        }
    }

    private void onServiceConnected(EventBus restEventBus) {
        this.restEventBus = restEventBus;
        this.restEventBus.register(this);
        this.restEventBus.post(new StartRestEvent(totalTimeMilliseconds));
    }

    @Override
    public void onDetach() {
        Timber.d("onDetach");
        super.onDetach();

        progressBarAnimation.stop();
        restEventBus.unregister(this);
        restEventBus = null;
        serviceBindController.unbindService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_actual_training_rest, container, false);

        totalTimeMilliseconds = getArguments().getLong(REST_TIME_MILLISECONDS_KEY);
        setTimeLeft(totalTimeMilliseconds);

        initListeners();
        initAnimations();

        setViewState(State.IN_PROGRESS);

        return binding.getRoot();
    }

    private void initAnimations() {
        progressBarAnimation = new SatelliteProgressBarAnimation(binding.satelliteProgressBar, totalTimeMilliseconds);

        fadeInFadeOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        fadeInFadeOut.setRepeatMode(REVERSE);
        fadeInFadeOut.setRepeatCount(INFINITE);
    }

    private void initListeners() {
        binding.restPlusOneMinuteButton.setOnClickListener(this::onPlusOneMinuteButtonClick);
        binding.restStopButton.setOnClickListener(this::onStopRestButtonClick);
        binding.restPauseResumeActionButton.setOnClickListener(this::onPauseResumeButtonClick);
    }

    @Subscribe
    private void onTick(RestTimerTickEvent event) {
        setTimeLeft(event.getMilliseconds());
    }

    @Subscribe
    private void onPause(RestPausedEvent event) {
        setViewState(State.PAUSED);
    }

    @Subscribe
    private void onResume(RestResumedEvent event) {
        setViewState(State.IN_PROGRESS);
    }

    @Subscribe
    private void onRestTimeAdded(RestTimeAddedEvent event) {
        setTimeLeft(event.getMilliseconds());
        progressBarAnimation.setDuration(event.getMilliseconds());
    }

    @Subscribe
    private void onRestFinished(RestFinishedEvent event) {
        setTimeLeft(0);
        setViewState(State.FINISHED);
    }

    private void setTimeLeft(long milliseconds) {
        binding.setCurrentTime(TIME_FORMATTER.format(new Date(milliseconds)));
    }

    private void setViewState(State state) {
        getActivity().runOnUiThread(() -> {
            @DrawableRes int playPauseButtonDrawable;

            switch (state) {
                case PAUSED:
                    progressBarAnimation.pause();
                    playPauseButtonDrawable = R.drawable.ic_play_arrow_white_24dp;
//                    binding.restTimeLeft.startAnimation(fadeInFadeOut);
                    break;

                case IN_PROGRESS:
                    progressBarAnimation.start();
                    playPauseButtonDrawable = R.drawable.ic_pause_white_24dp;
                    binding.restTimeLeft.clearAnimation();
                    break;

                case FINISHED:
                    progressBarAnimation.stop();
                    playPauseButtonDrawable = R.drawable.ic_stop_white_24dp;
//                    binding.restTimeLeft.startAnimation(fadeInFadeOut);
                    break;

                default:
                    throw new IllegalStateException();
            }

            binding.restPauseResumeActionButton.setImageResource(playPauseButtonDrawable);
            binding.setState(state);
        });
    }

    private void onPlusOneMinuteButtonClick(View view) {
        restEventBus.post(new AddRestTimeEvent(60000));
    }

    private void onStopRestButtonClick(View view) {
        restEventBus.post(new StopRestEvent());
        callback.onRestStopped();
    }

    private void onPauseResumeButtonClick(View view) {
        switch (binding.getState()) {
            case PAUSED:
                restEventBus.post(new ResumeRestEvent());
                break;
            case IN_PROGRESS:
                restEventBus.post(new PauseRestEvent());
                break;
            case FINISHED:
                onStopRestButtonClick(null);
                break;
        }
    }
}
