package ru.codingworkshop.gymm.ui.info.exercise;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.databinding.BindingConversion;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import ru.codingworkshop.gymm.App;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.ExerciseEntity;
import ru.codingworkshop.gymm.data.model.MuscleGroup;
import ru.codingworkshop.gymm.databinding.ActivityExerciseInfoBinding;
import ru.codingworkshop.gymm.ui.util.HiddenTitleAppBarBehavior;

public class ExerciseInfoActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Uri>,
        Callback {

    private static Boolean IS_YOUTUBE_SERVICE_SUPPORTED;

    private static final String YOUTUBE_DEVELOPER_KEY = "AIzaSyCnjhekaG5JdIEtdbeMH4iE0pZiprQZYp4";
    private static final String YOUTUBE_EMBED_URI = "https://www.youtube.com/embed/";
    private static final String GOOGLE_API_YOUTUBE_SNIPPET_URI = "https://www.googleapis.com/youtube/v3/videos?key=%s&part=snippet&id=%s";
    private static final String TAG = ExerciseInfoActivity.class.getSimpleName();
    public static final String EXERCISE_ID = "exerciseId";

    private Exercise model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityExerciseInfoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_exercise_info);

        if (IS_YOUTUBE_SERVICE_SUPPORTED == null)
            IS_YOUTUBE_SERVICE_SUPPORTED = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this) == YouTubeInitializationResult.SUCCESS;

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXERCISE_ID)) {
            long exerciseId = intent.getLongExtra(EXERCISE_ID, 0L);
            model = ((App) getApplication()).getData().select(Exercise.class).where(ExerciseEntity.ID.eq(exerciseId)).get().first();
            binding.setExercise(model);
        } else {
            throw new IllegalArgumentException("No argument for " + this.getClass().getName());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.exercise_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // hide toolbar above video thumbnail but show when scroll up above text
        new OnOffsetChangedListener(toolbar, model.getName());

        // setting difficulty
        ImageView difficultyImage = ((ImageView) findViewById(R.id.exercise_info_activity_difficulty));
        // for 0 already set in the xml
        if (model.getDifficulty() == 1)
            difficultyImage.setImageResource(R.drawable.ic_signal_cellular_2_bar_primary_24dp);
        else if (model.getDifficulty() == 2)
            difficultyImage.setImageResource(R.drawable.ic_signal_cellular_4_bar_primary_24dp);

        // loading youtube thumbnail
        final String youTubeVideo = model.getYouTubeVideo();
        if (youTubeVideo != null && youTubeVideo.length() != 0) {
            findViewById(R.id.exercise_info_activity_progress_bar).setVisibility(View.VISIBLE);

            LoaderManager loaderManager = getLoaderManager();
            int loaderId = 0;
            Bundle args = new Bundle(1);
            args.putString(VideoSnippetAsyncLoader.YOUTUBE_VIDEO_ID_KEY, youTubeVideo);
            if (loaderManager.getLoader(loaderId) == null)
                loaderManager.initLoader(loaderId, args, this);
            else
                loaderManager.restartLoader(loaderId, args, this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onVideoClick(View v) {
        String videoId = model.getYouTubeVideo();

        Log.d(TAG, "onVideoClick: " + videoId);

        Intent playVideoIntent;
        if (IS_YOUTUBE_SERVICE_SUPPORTED) {
            playVideoIntent = YouTubeStandalonePlayer.createVideoIntent(this, YOUTUBE_DEVELOPER_KEY, videoId, 0, true, false);
        } else {
            Uri youtubeVideoUri = Uri.parse(YOUTUBE_EMBED_URI + videoId);
            playVideoIntent = new Intent(Intent.ACTION_VIEW, youtubeVideoUri);
        }

        startActivity(playVideoIntent);
    }

    @Override
    public Loader<Uri> onCreateLoader(int id, final Bundle args) {
        return new VideoSnippetAsyncLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<Uri> loader, Uri data) {
        if (data != null) {
            double ratio = 9/16.0;
            int imageWidth = getWindow().getDecorView().getRootView().getWidth();
            int imageHeight = (int) (imageWidth * ratio);

            ImageView thumbnailImageView = (ImageView) findViewById(R.id.exercise_info_activity_appbar_image);
            thumbnailImageView.setMinimumHeight(imageHeight);

            RequestCreator creator = Picasso.with(this)
                    .load(data)
                    .resize(imageWidth, imageHeight)
                    .centerCrop();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                creator = creator.error(R.drawable.sporty_woman);
            }

            creator.into(thumbnailImageView, this);
        }
    }

    @Override
    public void onLoaderReset(Loader<Uri> loader) {

    }

    @Override
    public void onSuccess() {
        findViewById(R.id.exercise_info_activity_progress_bar).setVisibility(View.GONE);
        findViewById(R.id.exercise_info_activity_play_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onError() {
        findViewById(R.id.exercise_info_activity_progress_bar).setVisibility(View.GONE);
        findViewById(R.id.exercise_info_activity_play_button).setVisibility(View.VISIBLE);
    }

    public static Uri makeUriForDownloader(String videoId) {
        return Uri.parse(String.format(GOOGLE_API_YOUTUBE_SNIPPET_URI, YOUTUBE_DEVELOPER_KEY, videoId));
    }

    @BindingConversion
    public static String convertMuscleGroupsToString(List<MuscleGroup> muscleGroups) {
        if (muscleGroups == null || muscleGroups.isEmpty())
            return "";

        StringBuilder builder = new StringBuilder(2 * muscleGroups.size()); // for elements and dividers
        String divider = " • ";

        for (MuscleGroup g : muscleGroups)
            builder.append(g.getName()).append(divider);

        String built = builder.toString();

        return built.substring(0, built.length() - 2);
    }

    private static final class OnOffsetChangedListener extends HiddenTitleAppBarBehavior {
        private final @ColorInt int toolbarBackground;
        private final @ColorInt int transparent;

        OnOffsetChangedListener(Toolbar toolbar, String title) {
            super(toolbar, title);

            toolbarBackground = ((ColorDrawable) toolbar.getBackground()).getColor();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                transparent = toolbar.getResources().getColor(android.R.color.transparent);
            else
                transparent = toolbar.getResources().getColor(android.R.color.transparent, null);
        }

        @Override
        protected void setVisible(boolean visible) {
            toolbar.setBackgroundColor(visible ? toolbarBackground : transparent);
            super.setVisible(visible);
        }
    }
}
