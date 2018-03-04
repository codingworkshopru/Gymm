package ru.codingworkshop.gymm.ui.info.exercise;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.databinding.FragmentExerciseInfoBinding;

public class ExerciseInfoFragment extends DialogFragment {
    public static final String TAG = "exerciseInfoFragment";
    static final String EXERCISE_ID_KEY = "exerciseIdKey";

    public interface OnYouTubeVideoIdReadyCallback {
        void youTubeVideoIdReady(String videoId);
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private FragmentExerciseInfoBinding binding;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exercise_info, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ExerciseInfoFragmentViewModel viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ExerciseInfoFragmentViewModel.class);

        viewModel.load(getArguments().getLong(EXERCISE_ID_KEY))
                .observe(this, this::onExerciseNodeLoaded);
    }

    private void onExerciseNodeLoaded(ExerciseNode exerciseNode) {
        Exercise exercise = exerciseNode.getParent();
        String youTubeVideoId = exercise.getYouTubeVideo();
        if (!TextUtils.isEmpty(youTubeVideoId) && getActivity() instanceof OnYouTubeVideoIdReadyCallback) {
            ((OnYouTubeVideoIdReadyCallback) getActivity()).youTubeVideoIdReady(youTubeVideoId);
        }
        binding.setExercise(exercise);
        binding.setPrimaryMuscleGroup(exerciseNode.getPrimaryMuscleGroup());
        List<String> secondaryMuscleGroupNames = Lists.transform(exerciseNode.getChildren(), MuscleGroup::getName);
        binding.setSecondaryMuscleGroups(TextUtils.join(" • ", secondaryMuscleGroupNames));
    }


    public static ExerciseInfoFragment newInstance(long exerciseId) {
        Bundle args = new Bundle(1);
        args.putLong(EXERCISE_ID_KEY, exerciseId);

        ExerciseInfoFragment fragment = new ExerciseInfoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static String insertBullets(String value) {
        if (value == null || value.length() == 0)
            return value;

        Pattern pattern = Pattern.compile("^+", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(value);
        return !matcher.find(1) ? value : matcher.replaceAll("• ");
    }
}