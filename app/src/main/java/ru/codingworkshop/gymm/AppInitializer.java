package ru.codingworkshop.gymm;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.db.initializer.Initializer;

/**
 * Created by Радик on 18.06.2017.
 */

@Singleton
public class AppInitializer implements Initializer {
    private Map<Integer, Initializer> initializerMap;
    private MutableLiveData<Boolean> initialized = new MutableLiveData<>();

    @Inject
    AppInitializer(Map<Integer, Initializer> commands) {
        this.initializerMap = new TreeMap<>(commands);
    }

    public MutableLiveData<Boolean> getInitialized() {
        return initialized;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void initialize() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                for (Initializer initializer : initializerMap.values()) {
                    initializer.initialize();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                initialized.setValue(true);
            }
        }.execute();
    }
}
