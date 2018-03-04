package ru.codingworkshop.gymm.ui.info.exercise;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.databinding.ActivityExerciseInfoBinding;

public class ExerciseInfoActivity extends AppCompatActivity implements
        ExerciseInfoFragment.OnYouTubeVideoIdReadyCallback,
        HasSupportFragmentInjector
{
    public static final String EXERCISE_ID_KEY = "exerciseIdKey";

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    private static final String GOOGLE_DEVELOPER_KEY = "AIzaSyCnjhekaG5JdIEtdbeMH4iE0pZiprQZYp4";
    private YouTubeThumbnailView youTubeThumbnailView;
    private YouTubeThumbnailLoader youTubeThumbnailLoader;
    private String youTubeVideoId;
    private ActivityExerciseInfoBinding exerciseInfoBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        exerciseInfoBinding = DataBindingUtil.setContentView(this, R.layout.activity_exercise_info);
        youTubeThumbnailView = exerciseInfoBinding.exerciseInfoVideoThumbnail;

        setDefaultThumbnail();

        addExerciseInfoFragment();
    }

    private void addExerciseInfoFragment() {
        ExerciseInfoFragment fragment = (ExerciseInfoFragment) getSupportFragmentManager().findFragmentByTag(ExerciseInfoFragment.TAG);
        if (fragment == null) {
            fragment = ExerciseInfoFragment.newInstance(getIntent().getLongExtra(EXERCISE_ID_KEY, 0L));
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.exerciseInfoActivityInfoContainer, fragment, ExerciseInfoFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (youTubeThumbnailLoader != null) {
            youTubeThumbnailLoader.release();
            youTubeThumbnailLoader = null;
        }
    }

    @Override
    public void youTubeVideoIdReady(String videoId) {
        if (youTubeThumbnailView == null || TextUtils.isEmpty(videoId)) {
            showVideoNotExists();
        }

        youTubeVideoId = videoId;

        youTubeThumbnailView.initialize(GOOGLE_DEVELOPER_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader thumbnailLoader) {
                youTubeThumbnailLoader = thumbnailLoader;
                youTubeThumbnailLoader.setVideo(youTubeVideoId);
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        exerciseInfoBinding.setVideoLoaded(true);
                        youTubeThumbnailLoader.setOnThumbnailLoadedListener(null);
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        showVideoLoadingError();
                        youTubeThumbnailLoader.setOnThumbnailLoadedListener(null);
                        youTubeThumbnailLoader.release();
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                youTubeInitializationResult.getErrorDialog(ExerciseInfoActivity.this, 0).show();
                setDefaultThumbnail();
            }
        });
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    private void setDefaultThumbnail() {
        youTubeThumbnailView.setImageResource(R.drawable.sporty_woman);
    }

    public void onPlayVideoButtonClick(View button) {
        if (!TextUtils.isEmpty(youTubeVideoId)) {
            Intent videoIntent = YouTubeStandalonePlayer.createVideoIntent(this, GOOGLE_DEVELOPER_KEY, youTubeVideoId, 0, true, false);
            startActivity(videoIntent);
        }
    }

    private void showVideoNotExists() {
        showVideoError(R.string.exercise_info_video_not_exists);
    }

    private void showVideoLoadingError() {
        showVideoError(R.string.exericse_info_video_loading_error);
    }

    private void showVideoError(@StringRes int error) {
        TextView errorTextView = exerciseInfoBinding.exerciseInfoVideoError;
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(error);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
