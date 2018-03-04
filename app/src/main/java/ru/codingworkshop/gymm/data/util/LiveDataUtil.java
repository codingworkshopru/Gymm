package ru.codingworkshop.gymm.data.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

/**
 * Created by Радик on 31.05.2017.
 */

public final class LiveDataUtil {
    private LiveDataUtil() {}

    public static <T> LiveData<T> getLive(T value) {
        MutableLiveData<T> result = new MutableLiveData<>();
        result.setValue(value);
        return result;
    }

    public static <T> LiveData<T> getAbsent() {
        return getLive(null);
    }

    public static <T> LiveData<T> getOnce(LiveData<T> liveData, Consumer<T> a) {
        liveData.observeForever(new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                a.accept(t);
                liveData.removeObserver(this);
            }
        });

        return liveData;
    }

}
