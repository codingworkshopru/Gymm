package ru.codingworkshop.gymm.info.exercise;

import android.content.Intent;
import android.databinding.BindingConversion;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.MuscleGroup;
import ru.codingworkshop.gymm.databinding.ActivityExerciseInfoBinding;

public class ExerciseInfoActivity extends AppCompatActivity implements Picasso.Listener {

    private static Boolean IS_YOUTUBE_SERVICE_SUPPORTED;

    private static final String YOUTUBE_DEVELOPER_KEY = "AIzaSyCnjhekaG5JdIEtdbeMH4iE0pZiprQZYp4";
    private static final String YOUTUBE_EMBED_URI = "https://www.youtube.com/embed/";
    private static final String GOOGLE_API_YOUTUBE_SNIPPET_URI = "https://www.googleapis.com/youtube/v3/videos?key=%s&part=snippet&id=%s";
    public static final String EXERCISE_ARG = Exercise.class.getCanonicalName();
    private static final String TAG = ExerciseInfoActivity.class.getSimpleName();

    private Exercise mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityExerciseInfoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_exercise_info);

        if (IS_YOUTUBE_SERVICE_SUPPORTED == null)
            IS_YOUTUBE_SERVICE_SUPPORTED = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this) == YouTubeInitializationResult.SUCCESS;

        setSupportActionBar((Toolbar) findViewById(R.id.exercise_info_toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXERCISE_ARG)) {
            mModel = intent.getParcelableExtra(EXERCISE_ARG);
            binding.setExercise(mModel);
        } else {
            throw new IllegalArgumentException("No argument for " + this.getClass().getName());
        }

        if (mModel.hasVideo()) {
            Picasso picasso = new Picasso.Builder(this)
                    .downloader(new YouTubeThumbnailsDownloader(this))
                    .build();

            Uri uri = makeUriForDownloader(mModel.getVideo());
            picasso.load(uri)
                    .placeholder(R.drawable.sporty_woman)
                    .into((ImageView) findViewById(R.id.exercise_info_activity_appbar_image));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onVideoClick(View v) {
        String videoId = mModel.getVideo();

        Log.d(TAG, "onVideoClick: " + videoId);

        Intent intent;
        if (IS_YOUTUBE_SERVICE_SUPPORTED) {
            intent = YouTubeStandalonePlayer.createVideoIntent(this, YOUTUBE_DEVELOPER_KEY, videoId, 0, true, false);
        } else {
            Uri youtubeVideoUri = Uri.parse(YOUTUBE_EMBED_URI + videoId);
            intent = new Intent(Intent.ACTION_VIEW, youtubeVideoUri);
        }

        startActivity(intent);
    }

    public static Uri makeUriForDownloader(String videoId) {
        return Uri.parse(String.format(GOOGLE_API_YOUTUBE_SNIPPET_URI, YOUTUBE_DEVELOPER_KEY, videoId));
    }

    @Override
    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
        Log.d(TAG, "onImageLoadFailed:"+e.getMessage() + "; Uri:"+uri.toString());
    }

    @BindingConversion
    public static String convertMuscleGroupsToString(List<MuscleGroup> muscleGroups) {
        if (muscleGroups == null || muscleGroups.isEmpty())
            return "";

        StringBuilder builder = new StringBuilder(2 * muscleGroups.size()); // for elements and dividers
        String divider = " \u2022 ";

        for (MuscleGroup g : muscleGroups)
            builder.append(g.getName()).append(divider);

        String built = builder.toString();

        return built.substring(0, built.length() - 2);
    }
}
