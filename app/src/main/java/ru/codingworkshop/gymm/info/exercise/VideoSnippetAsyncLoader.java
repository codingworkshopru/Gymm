package ru.codingworkshop.gymm.info.exercise;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.UrlConnectionDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Радик on 25.04.2017.
 */
class VideoSnippetAsyncLoader extends AsyncTaskLoader<Uri> {
    static final String YOUTUBE_VIDEO_ID_KEY = "youTubeVideoIdKey";

    private final Bundle args;
    private Uri uri;

    public VideoSnippetAsyncLoader(Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if (uri == null)
            forceLoad();
        else
            deliverResult(uri);
    }

    @Override
    public Uri loadInBackground() {
        UrlConnectionDownloader downloader = new UrlConnectionDownloader(getContext());
        String videoId = args.getString(YOUTUBE_VIDEO_ID_KEY);
        Uri snippetUri = ExerciseInfoActivity.makeUriForDownloader(videoId);

        try {
            Downloader.Response resp = downloader.load(snippetUri, 0);
            InputStreamReader streamReader = new InputStreamReader(resp.getInputStream(), Charsets.UTF_8);
            String jsonString = CharStreams.toString(streamReader);

            JSONObject response = new JSONObject(jsonString);
            JSONArray items = response.getJSONArray("items");
            JSONObject firstItem = items.getJSONObject(0);
            JSONObject snippet = firstItem.getJSONObject("snippet");
            JSONObject thumbnails = snippet.getJSONObject("thumbnails");

            String keyForImage = "default";
            if (thumbnails.has("maxres")) {
                keyForImage = "maxres";
            } else if (thumbnails.has("standard")) {
                keyForImage = "standard";
            } else if (thumbnails.has("high")) {
                keyForImage = "high";
            } else if (thumbnails.has("medium")) {
                keyForImage = "medium";
            }

            JSONObject youTubeVideo = thumbnails.getJSONObject(keyForImage);

            uri = Uri.parse(youTubeVideo.getString("url"));

            streamReader.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }

        return uri;
    }
}
