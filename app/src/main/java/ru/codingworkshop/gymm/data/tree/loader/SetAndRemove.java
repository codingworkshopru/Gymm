package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import ru.codingworkshop.gymm.data.util.Consumer;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */
public final class SetAndRemove {
    private int countdown;
    private MutableLiveData<Boolean> loaded = new MutableLiveData<>();

    public SetAndRemove(int countdown) {
        this.countdown = countdown;
        loaded.setValue(false);
    }

    public <T> void ok(LiveData<T> liveData, Consumer<T> consumer) {
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                consumer.accept(t);
                liveData.removeObserver(this);
                if (--countdown == 0) {
                    loaded.setValue(true);
                }
            }
        };
        liveData.observeForever(observer);
    }

    public MutableLiveData<Boolean> getLoaded() {
        return loaded;
    }
}
