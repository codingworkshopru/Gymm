package ru.codingworkshop.gymm.ui.info.exercise;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.common.collect.Lists;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.databinding.FragmentExerciseInfoBinding;

import static com.google.android.youtube.player.YouTubeInitializationResult.SUCCESS;

public class ExerciseInfoFragment extends Fragment {
    static final String EXERCISE_ID_KEY = "exerciseIdKey";
    private static final String GOOGLE_DEVELOPER_KEY = "AIzaSyCnjhekaG5JdIEtdbeMH4iE0pZiprQZYp4";
    private static final String YOU_TUBE_PLAYER_TAG = "youTubePlayerTag";

    @VisibleForTesting
    ViewModelProvider.Factory viewModelFactory;
    private ExerciseInfoFragmentViewModel viewModel;
    private FragmentExerciseInfoBinding binding;
    private YouTubePlayerSupportFragment youTubePlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = viewModelFactory.create(ExerciseInfoFragmentViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exercise_info, container, false);

        LiveData<ExerciseNode> liveNode = viewModel.load(getArguments().getLong(EXERCISE_ID_KEY));
        liveNode.observe(this, this::onExerciseNodeLoaded);

        return binding.getRoot();
    }

    private void onExerciseNodeLoaded(ExerciseNode exerciseNode) {
        Exercise exercise = exerciseNode.getParent();
        String youTubeVideoId = exercise.getYouTubeVideo();
        YouTubeInitializationResult youTubeInitializationResult = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getContext());
        if (youTubeInitializationResult != SUCCESS || TextUtils.isEmpty(youTubeVideoId)) {
            invalidateYouTubePlayer();
        } else {
            initYouTubePlayer(youTubeVideoId);
        }

        binding.setExercise(exercise);
        binding.setPrimaryMuscleGroup(exerciseNode.getPrimaryMuscleGroup());
        List<String> secondaryMuscleGroupNames = Lists.transform(exerciseNode.getChildren(), MuscleGroup::getName);
        binding.setSecondaryMuscleGroups(TextUtils.join(" • ", secondaryMuscleGroupNames));
    }

    private void initYouTubePlayer(final String videoId) {
        youTubePlayer = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentByTag(YOU_TUBE_PLAYER_TAG);
        if (youTubePlayer == null) {
            youTubePlayer = YouTubePlayerSupportFragment.newInstance();

            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.exerciseInfoYouTubePlayerContainer, youTubePlayer, YOU_TUBE_PLAYER_TAG)
                    .commit();
        }

        this.youTubePlayer.initialize(GOOGLE_DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
//                youTubePlayer.setFullscreen(false);
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
        if (youTubePlayer != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(youTubePlayer)
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
}