package ru.codingworkshop.gymm.ui.actual;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import ru.codingworkshop.gymm.service.event.incoming.StartRestEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimerTickEvent;

public class ActualTrainingRestFragment extends Fragment {
    private static final String REST_TIME_MILLISECONDS_KEY = "restTimeMillisecondsKey";
    private final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("m:ss", Locale.getDefault());

    private FragmentActualTrainingRestBinding binding;
    private long totalTimeMilliseconds;
    @VisibleForTesting
    EventBus restEventBus;

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
        initBinding();

        ObjectAnimator animation = ObjectAnimator.ofFloat(binding.animatedLoadingView, "angle", 0f, 360f);
        animation.setDuration(totalTimeMilliseconds);
        animation.setInterpolator(new LinearInterpolator());
        animation.start();

        return binding.getRoot();
    }

    private void initBinding() {
        int totalTimeSeconds = (int) (totalTimeMilliseconds / 1000);
        binding.setTotalSeconds(totalTimeSeconds);
        binding.setCurrentTimeSeconds(totalTimeSeconds);
        setTimeLeft(totalTimeMilliseconds);
    }

    @Subscribe
    private void tick(RestTimerTickEvent event) {
        setTimeLeft(event.getMilliseconds());
    }

    private void setTimeLeft(long milliseconds) {
        binding.setCurrentTime(TIME_FORMATTER.format(new Date(milliseconds)));
    }
}
