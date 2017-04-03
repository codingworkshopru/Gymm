package ru.codingworkshop.gymm.info.exercise;

import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.UrlConnectionDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by Радик on 03.04.2017.
 */

class YouTubeThumbnailsDownloader extends UrlConnectionDownloader {

    YouTubeThumbnailsDownloader(Context context) {
        super(context);
    }

    @Override
    public Downloader.Response load(Uri uri, int networkPolicy) throws IOException {
        Downloader.Response resp = super.load(uri, networkPolicy);

        InputStreamReader streamReader = new InputStreamReader(resp.getInputStream(), "UTF-8");
        Scanner s = new Scanner(new BufferedReader(streamReader));

        StringBuilder jsonString = new StringBuilder();

        while (s.hasNext())
            jsonString.append(s.nextLine());

        Uri realImageUri;
        try {
            JSONObject response = new JSONObject(jsonString.toString());
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
            JSONObject objectWithImageUri = thumbnails.getJSONObject(keyForImage);
            realImageUri = Uri.parse(objectWithImageUri.getString("url"));
        } catch (JSONException e) {
            System.out.println(jsonString);
            e.printStackTrace();
            return null;
        }

        return super.load(realImageUri, networkPolicy);
    }
}
