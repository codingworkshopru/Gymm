package ru.codingworkshop.gymm.data.tree.loader.common;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

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
        liveLoaded.addSource(live, new Observer<T>() {
            private T data;

            @Override
            public void onChanged(@Nullable T t) {
                if (t != null && data == t) {
                    return;
                }
                data = t;
                sourcesCounter--;
                observer.onChanged(t);
                if (liveLoaded.hasActiveObservers()) {
                    if (sourcesCounter <= 0) {
                        LoaderDelegate.this.onAllSourcesLoaded();
                    }
                }
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
