package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;

import com.google.common.base.Supplier;

import ru.codingworkshop.gymm.data.util.BiConsumer;

/**
 * Created by Радик on 21.06.2017.
 */

public class Loader<Wrapper> {
    private Wrapper wrapper;
    private MediatorLiveData<Wrapper> liveWrapper;
    private boolean initialized;
    private int countdown;

    public Loader(Supplier<Wrapper> wrapperSupplier) {
        liveWrapper = new MediatorLiveData<>();
        wrapper = wrapperSupplier.get();
    }

    public LiveData<Wrapper> load() {
        return liveWrapper;
    }

    public <T> void addSource(LiveData<T> source, BiConsumer<Wrapper, T> resultSetter) {
        addSource(source, loaded -> resultSetter.accept(wrapper, loaded));
    }

    public <T> void addSource(LiveData<T> source, BiConsumer<Wrapper, T> resultSetter, Runnable ifNullReturned) {
        addSource(source, loaded -> {
            if (loaded != null) {
                resultSetter.accept(wrapper, loaded);
            } else {
                ifNullReturned.run();
            }
        });
    }

    public <T, F> void addDependentSource(LiveData<T> dependency, Function<T, LiveData<F>> sourceGetter, BiConsumer<Wrapper, F> resultSetter) {
        LiveData<F> transformed = Transformations.switchMap(dependency, sourceGetter);
        addSource(transformed, loaded -> {
            if (loaded != null) {
                resultSetter.accept(wrapper, loaded);
            }
        });
    }

    private <T> void addSource(LiveData<T> source, Observer<T> observer) {
        countdown++;
        liveWrapper.addSource(source, loaded -> {
            observer.onChanged(loaded);
            afterSourceLoaded();
        });
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
