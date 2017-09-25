package ru.codingworkshop.gymm.ui.actual;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.databinding.FragmentActualTrainingRestBinding;

public class ActualTrainingRestFragment extends Fragment {
    private static final String REST_TIME_SECONDS_KEY = "restTimeSecondsKey";

    private FragmentActualTrainingRestBinding binding;
    private int totalTimeSeconds;

    public static ActualTrainingRestFragment newInstance(int restTimeMillis) {
        ActualTrainingRestFragment fragment = new ActualTrainingRestFragment();
        Bundle arguments = new Bundle(1);
        arguments.putInt(REST_TIME_SECONDS_KEY, restTimeMillis);
        fragment.setArguments(arguments);
        return fragment;
    }

    public ActualTrainingRestFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_actual_training_rest, container, false);

        initBinding();

        ObjectAnimator animation = ObjectAnimator.ofFloat(binding.animatedLoadingView, "angle", 0f, 360f);
        animation.setDuration(90000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.start();

        return binding.getRoot();
    }

    private void initBinding() {
        totalTimeSeconds = getArguments().getInt(REST_TIME_SECONDS_KEY);

        binding.setTotalSeconds(totalTimeSeconds);
        binding.setCurrentTimeSeconds(totalTimeSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
        binding.setCurrentTime(sdf.format(new Date(totalTimeSeconds * 1000)));
    }
}
