package ru.codingworkshop.gymm.ui.actual;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.databinding.FragmentActualTrainingRestBinding;
import ru.codingworkshop.gymm.service.RestEventBusHolder;
import ru.codingworkshop.gymm.service.event.incoming.AddRestTimeEvent;
import ru.codingworkshop.gymm.service.event.incoming.PauseRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.ResumeRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.StartRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.StopRestEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestFinishedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestPausedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestResumedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestStoppedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimeAddedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimerTickEvent;

public class ActualTrainingRestFragment extends Fragment {
    private static final String REST_TIME_MILLISECONDS_KEY = "restTimeMillisecondsKey";
    private final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("m:ss", Locale.getDefault());

    private FragmentActualTrainingRestBinding binding;
    private long totalTimeMilliseconds;
    private boolean paused;
    @VisibleForTesting
    EventBus restEventBus;
    private ObjectAnimator animation;

    public enum State {
        PAUSED, IN_PROGRESS, FINISHED
    }

    public static ActualTrainingRestFragment newInstance(long restTimeMillis) {
        ActualTrainingRestFragment fragment = new ActualTrainingRestFragment();
        Bundle arguments = new Bundle(1);
        arguments.putLong(REST_TIME_MILLISECONDS_KEY, restTimeMillis);
        fragment.setArguments(arguments);

        return fragment;
    }

    public ActualTrainingRestFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (restEventBus == null) {
            if (context instanceof RestEventBusHolder) {
                restEventBus = ((RestEventBusHolder) context).getRestEventBus();
            } else {
                throw new IllegalStateException("Activity must implement "
                        + RestEventBusHolder.class.getSimpleName());
            }
        }

        restEventBus.register(this);
    }

    @Override
    public void onDetach() {
        animation.end();
        restEventBus.unregister(this);
        restEventBus = null;

        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_actual_training_rest, container, false);

        totalTimeMilliseconds = getArguments().getLong(REST_TIME_MILLISECONDS_KEY);
        restEventBus.post(new StartRestEvent(totalTimeMilliseconds));
        setTimeLeft(totalTimeMilliseconds);

        initListeners();
        initAnimation();

        setViewState(State.IN_PROGRESS);

        return binding.getRoot();
    }

    private void initAnimation() {
        animation = ObjectAnimator.ofFloat(binding.animatedLoadingView, "angle", 0f, 360f);
        animation.setDuration(totalTimeMilliseconds);
        animation.setInterpolator(new LinearInterpolator());
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
        animation.setDuration(event.getMilliseconds());
    }

    @Subscribe
    private void onRestFinished(RestFinishedEvent event) {
        setTimeLeft(0);
        setViewState(State.FINISHED);
    }

    @Subscribe
    private void onRestStopped(RestStoppedEvent event) {
        // TODO call an activity method which will detach the fragment
    }

    private void setTimeLeft(long milliseconds) {
        binding.setCurrentTime(TIME_FORMATTER.format(new Date(milliseconds)));
    }

    private void onPlusOneMinuteButtonClick(View view) {
        restEventBus.post(new AddRestTimeEvent(60000));
    }

    private void onStopRestButtonClick(View view) {
        restEventBus.post(new StopRestEvent());
    }

    private void onPauseResumeButtonClick(View view) {
        setPaused(!paused);
    }

    private void setPaused(boolean pausedFlag) {
        paused = pausedFlag;
        restEventBus.post(paused ? new PauseRestEvent() : new ResumeRestEvent());
    }

    private void setViewState(State state) {
        getActivity().runOnUiThread(() -> {
            @DrawableRes int playPauseButtonDrawable;

            switch (state) {
                case PAUSED:
                    animation.cancel();
                    playPauseButtonDrawable = R.drawable.ic_play_arrow_white_24dp;
                    break;

                case IN_PROGRESS:
                    animation.setupStartValues();
                    animation.start();
                    playPauseButtonDrawable = R.drawable.ic_pause_white_24dp;
                    break;

                case FINISHED:
                    animation.end();
                    playPauseButtonDrawable = R.drawable.ic_stop_white_24dp;

//                    Animation fadeInFadeOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
//                    fadeInFadeOut.setRepeatMode(REVERSE);
//                    fadeInFadeOut.setRepeatCount(INFINITE);
//                    binding.restTimeLeft.startAnimation(fadeInFadeOut);
                    break;

                default:
                    throw new IllegalStateException();
            }

            binding.restPauseResumeActionButton.setImageResource(playPauseButtonDrawable);
            binding.setState(state);
        });
    }

}
