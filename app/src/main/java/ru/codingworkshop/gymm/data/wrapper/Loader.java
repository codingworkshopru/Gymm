package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;

import java.lang.reflect.Constructor;

import ru.codingworkshop.gymm.data.util.BiConsumer;

/**
 * Created by Радик on 21.06.2017.
 */

public class Loader<Wrapper> {
    private Wrapper wrapper;
    private MediatorLiveData<Wrapper> liveWrapper;
    private boolean initialized;
    private int countdown;

    public Loader(Class<Wrapper> wrapperClass) {
        liveWrapper = new MediatorLiveData<>();
        wrapper = instantiateWrapper(wrapperClass);
    }

    private Wrapper instantiateWrapper(Class<Wrapper> wrapperClass) {
        try {
            Constructor<Wrapper> constructor = wrapperClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T> void addSource(LiveData<T> source, BiConsumer<Wrapper, T> resultSetter) {
        countdown++;
        liveWrapper.addSource(source, loaded -> {
            resultSetter.accept(wrapper, loaded);
            afterSourceLoaded();
        });
    }

    public <T, F> void addDependentSource(LiveData<T> dependency, Function<T, LiveData<F>> sourceGetter, BiConsumer<Wrapper, F> resultSetter) {
        countdown++;
        LiveData<F> l = Transformations.switchMap(dependency, sourceGetter);
        liveWrapper.addSource(l, loaded -> {
            resultSetter.accept(wrapper, loaded);
            afterSourceLoaded();
        });
    }

    public LiveData<Wrapper> load() {
        return liveWrapper;
    }

    private void afterSourceLoaded() {
        if (!initialized) {
            countdown--;
            if (countdown == 0) {
                initialized = true;
            }
        }

        if (initialized) {
            liveWrapper.setValue(wrapper);
        }
    }
}
