package ru.codingworkshop.gymm.info.exercise;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.GymDbHelper;
import ru.codingworkshop.gymm.data.model.Exercise;

public class ExerciseInfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Exercise>, YouTubeThumbnailView.OnInitializedListener {
    YouTubeThumbnailView thumbnailView;
    YouTubeThumbnailLoader thumbnailLoader;

    private static Boolean IS_YOUTUBE_SERVICE_SUPPORTED;

    private static final String YOUTUBE_DEVELOPER_KEY = "AIzaSyCnjhekaG5JdIEtdbeMH4iE0pZiprQZYp4";
    private static final String YOUTUBE_EMBED_URI = "https://www.youtube.com/embed/";
    public static final String EXERCISE_ARG = Exercise.class.getCanonicalName();
    private static final String EXERCISE_KEY = EXERCISE_ARG + "_KEY";
    private static final String TAG = ExerciseInfoActivity.class.getSimpleName();

    private Exercise mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_info);

        if (IS_YOUTUBE_SERVICE_SUPPORTED == null)
            IS_YOUTUBE_SERVICE_SUPPORTED = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this) == YouTubeInitializationResult.SUCCESS;

        setSupportActionBar((Toolbar) findViewById(R.id.exercise_info_toolbar));

        thumbnailView = (YouTubeThumbnailView) findViewById(R.id.youtube_thumbnail_view);

        Bundle args = new Bundle(1);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXERCISE_ARG)) {
            args.putLong(EXERCISE_KEY, intent.getLongExtra(EXERCISE_ARG, 0));
        } else {
            throw new IllegalArgumentException("No argument for " + this.getClass().getName());
        }

        Loader loader = getSupportLoaderManager().getLoader(0);
        if (loader == null)
            getSupportLoaderManager().initLoader(0, args, this);
        else
            getSupportLoaderManager().restartLoader(0, args, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thumbnailLoader != null)
            thumbnailLoader.release();
    }

    @Override
    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
        if (thumbnailLoader != null)
            thumbnailLoader.release();

        thumbnailLoader = youTubeThumbnailLoader;
        thumbnailLoader.setVideo(mModel.getVideo());
        thumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {

            }

            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                Log.e(TAG, "onThumbnailError: " + errorReason);
            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
        String error = "The initialization attempt was successful.";
        switch (youTubeInitializationResult) {
            case CLIENT_LIBRARY_UPDATE_REQUIRED:
                error = "The version of the client library used to connect to the YouTube API service is out of date.";
                break;
            case DEVELOPER_KEY_INVALID:
                error = "The developer key which was supplied to the initialization function is invalid.";
                break;
            case ERROR_CONNECTING_TO_SERVICE:
                error = "There was an error connecting to the YouTube API service.";
                break;
            case INTERNAL_ERROR:
                error = "An internal error occurred.";
                break;
            case INVALID_APPLICATION_SIGNATURE:
                error = "The application's APK has been incorrectly signed.";
                break;
            case NETWORK_ERROR:
                error = "There was an error connecting to the network which prevented the YouTube Player API service initializing.";
                break;
            case SERVICE_DISABLED:
                error = "The installed version of the YouTube API service has been disabled on this device.";
                break;
            case SERVICE_INVALID:
                error = "The version of the YouTube API service installed on this device is not valid.";
                break;
            case SERVICE_MISSING:
                error = "The YouTube API service is missing on this device.";
                break;
            case SERVICE_VERSION_UPDATE_REQUIRED:
                error = "The installed version of YouTube API service is out of date.";
                break;
            case UNKNOWN_ERROR:
                error = "The reason for the error is not known.";
                break;
        }

        Log.e(TAG, error);
    }

    @Override
    public Loader<Exercise> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Exercise>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Exercise loadInBackground() {
                SQLiteOpenHelper dbHelper = new GymDbHelper(getContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                long exerciseId = args.getLong(EXERCISE_KEY);

                return Exercise.readOne(db, exerciseId);
            }
        };
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

    @Override
    public void onLoadFinished(Loader<Exercise> loader, Exercise data) {
        mModel = data;

        if (mModel.hasVideo()) {
            if (IS_YOUTUBE_SERVICE_SUPPORTED)
                thumbnailView.initialize(YOUTUBE_DEVELOPER_KEY, this);

            findViewById(R.id.imageButton3).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Exercise> loader) {
    }
}
