package ru.codingworkshop.gymm.data.tree.loader.common;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;

/**
 * Created by Radik on 09.11.2017.
 */

public class LoaderDelegate {
    private int sourcesCounter;
    private MediatorLiveData<Boolean> liveLoaded;

    public LoaderDelegate() {
        liveLoaded = new MediatorLiveData<>();
    }

    public <T> LiveData<T> mapLoaded(T node) {
        return Transformations.map(liveLoaded, loaded -> loaded != null && loaded ? node : null);
    }

    public <T> void addSource(LiveData<T> live, Observer<T> observer) {
        sourcesCounter++;
        liveLoaded.addSource(live, t -> {
            observer.onChanged(t);
            sourcesCounter--;
            if (sourcesCounter <= 0) {
                onAllSourcesLoaded();
            }
        });
    }

    /**
     * call <b>super.onAllSourcesLoaded</b> after all data processing
     */
    protected void onAllSourcesLoaded() {
        liveLoaded.setValue(true);
    }
}
