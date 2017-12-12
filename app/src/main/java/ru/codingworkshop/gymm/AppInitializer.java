package ru.codingworkshop.gymm;

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

    @Override
    public void initialize() {
        AsyncTask.execute(() -> {
            for (Initializer initializer : initializerMap.values()) {
                initializer.initialize();
            }
            initialized.postValue(true);
        });
    }
}
