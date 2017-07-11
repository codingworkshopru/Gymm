package ru.codingworkshop.gymm.db.initializer;

import android.content.Context;
import android.support.annotation.RawRes;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Created by Радик on 01.06.2017.
 */

abstract class EntityInitializer<T> implements Initializer {
    private Context context;

    EntityInitializer(Context context) {
        this.context = context;
    }

    @WorkerThread
    @Override
    public void initialize() {
        if (needToInitialize()) {
            BufferedReader bufferedReader = getReader();
            saveToDatabase(buildGson().fromJson(bufferedReader, getType()));
        }
    }

    private BufferedReader getReader() {
        InputStream stream = context.getResources().openRawResource(getJsonResourceId());
        InputStreamReader streamReader = new InputStreamReader(stream);
        return new BufferedReader(streamReader);
    }

    Gson buildGson() {
        return new Gson();
    }

    abstract boolean needToInitialize();
    abstract Type getType();
    abstract @RawRes int getJsonResourceId();
    abstract void saveToDatabase(T data);
}
