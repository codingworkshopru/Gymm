package ru.codingworkshop.gymm.ui.info.exercise;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import ru.codingworkshop.gymm.R;

import static com.google.android.youtube.player.YouTubeInitializationResult.SUCCESS;

public class ExerciseInfoActivity extends AppCompatActivity implements
        ExerciseInfoFragment.OnYouTubeVideoIdReadyCallback,
        HasSupportFragmentInjector
{
    public static final String EXERCISE_ID_KEY = "exerciseIdKey";

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    private static final String GOOGLE_DEVELOPER_KEY = "AIzaSyCnjhekaG5JdIEtdbeMH4iE0pZiprQZYp4";
    private static final String YOUTUBE_PLAYER_FRAGMENT_TAG = "youTubePlayerFragmentTag";
    private YouTubePlayerSupportFragment youTubePlayerFragment;

    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_info);

        addExerciseInfoFragment();
        addYouTubeVideoFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (youTubePlayer != null) {
            youTubePlayer.release();
            youTubePlayer = null;
        }
    }

    private void addYouTubeVideoFragment() {
        if (YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this) == SUCCESS) {
            youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentByTag(YOUTUBE_PLAYER_FRAGMENT_TAG);

            if (youTubePlayerFragment == null) {
                youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.exerciseInfoYouTubePlayerContainer, youTubePlayerFragment, YOUTUBE_PLAYER_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            findViewById(R.id.exerciseInfoStubImage).setVisibility(View.VISIBLE);
        }
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
    public void youTubeVideoIdReady(String videoId) {
        if (youTubePlayerFragment == null) return;

        youTubePlayerFragment.initialize(GOOGLE_DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                ExerciseInfoActivity.this.youTubePlayer = youTubePlayer;
                if (!wasRestored) {
                    youTubePlayer.cueVideo(videoId);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
