package ru.codingworkshop.gymm;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Радик on 19.06.2017.
 */

public class TestApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
