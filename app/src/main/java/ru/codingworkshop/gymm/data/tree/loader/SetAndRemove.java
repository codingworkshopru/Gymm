package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.util.Consumer;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */
public final class SetAndRemove {
    private int countdown;
    private MediatorLiveData<Boolean> liveLoaded = new MediatorLiveData<>();

    public <T> void ok(@NonNull LiveData<T> source, @NonNull Consumer<T> consumer) {
        countdown++;
        liveLoaded.addSource(source, t -> {
            liveLoaded.removeSource(source);
            consumer.accept(t);
            if (--countdown == 0) {
                liveLoaded.setValue(true);
            }
        });
    }

    public MutableLiveData<Boolean> getLoaded() {
        return liveLoaded;
    }
}
