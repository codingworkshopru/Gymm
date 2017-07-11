package ru.codingworkshop.gymm.data.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

/**
 * Created by Радик on 31.05.2017.
 */

public final class LiveDataUtil {
    public static <T> LiveData<T> getLive(T value) {
        MutableLiveData<T> result = new MutableLiveData<>();
        result.setValue(value);
        return result;
    }

    public static <T> LiveData<T> getAbsent() {
        return getLive(null);
    }
}
