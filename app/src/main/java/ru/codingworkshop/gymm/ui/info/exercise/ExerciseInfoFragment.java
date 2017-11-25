package ru.codingworkshop.gymm.ui.info.exercise;

import android.app.Dialog;
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
import android.view.Window;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.google.android.youtube.player.YouTubeInitializationResult.SUCCESS;

public class ExerciseInfoFragment extends DialogFragment {
    public static final String TAG = "exerciseInfoFragmentTag";
    static final String EXERCISE_ID_KEY = "exerciseIdKey";
    private static final String GOOGLE_DEVELOPER_KEY = "AIzaSyCnjhekaG5JdIEtdbeMH4iE0pZiprQZYp4";
    private static final String YOU_TUBE_PLAYER_FRAGMENT_TAG = "youTubePlayerFragmentTag";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private FragmentExerciseInfoBinding binding;
    private YouTubePlayerSupportFragment youTubePlayerFragment;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExerciseInfoFragmentViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(ExerciseInfoFragmentViewModel.class);
        viewModel.load(getArguments().getLong(EXERCISE_ID_KEY))
                .observe(this, this::onExerciseNodeLoaded);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exercise_info, container, false);
        YouTubeInitializationResult youTubeInitializationResult = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getContext());
        if (youTubeInitializationResult != SUCCESS) {
            invalidateYouTubePlayer();
        } else {
            initYouTubePlayerFragment();
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        youTubePlayerFragment = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(MATCH_PARENT, MATCH_PARENT);
            }
        }
        super.onStart();
    }

    private void onExerciseNodeLoaded(ExerciseNode exerciseNode) {
        Exercise exercise = exerciseNode.getParent();
        String youTubeVideoId = exercise.getYouTubeVideo();
        if (youTubePlayerFragment == null || TextUtils.isEmpty(youTubeVideoId)) {
            invalidateYouTubePlayer();
        } else {
            initYouTubePlayerWithVideo(youTubeVideoId);
        }
        binding.setExercise(exercise);
        binding.setPrimaryMuscleGroup(exerciseNode.getPrimaryMuscleGroup());
        List<String> secondaryMuscleGroupNames = Lists.transform(exerciseNode.getChildren(), MuscleGroup::getName);
        binding.setSecondaryMuscleGroups(TextUtils.join(" • ", secondaryMuscleGroupNames));
    }

    private void initYouTubePlayerFragment() {
        youTubePlayerFragment = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentByTag(YOU_TUBE_PLAYER_FRAGMENT_TAG);
        if (youTubePlayerFragment == null) {
            youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.exerciseInfoYouTubePlayerContainer, youTubePlayerFragment, YOU_TUBE_PLAYER_FRAGMENT_TAG)
                    .commit();
        }
    }

    private void initYouTubePlayerWithVideo(String videoId) {
        youTubePlayerFragment.initialize(GOOGLE_DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
//                youTubePlayerFragment.setFullscreen(false);
                youTubePlayer.cueVideo(videoId);
                youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        invalidateYouTubePlayer();
                    }

                    public void onLoading() {}
                    public void onLoaded(String s) {}
                    public void onAdStarted() {}
                    public void onVideoStarted() {}
                    public void onVideoEnded() {}
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                invalidateYouTubePlayer();
            }
        });
    }

    private void invalidateYouTubePlayer() {
        if (youTubePlayerFragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(youTubePlayerFragment)
                    .commit();
        }

        binding.exerciseInfoStubImage.setVisibility(View.VISIBLE);
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