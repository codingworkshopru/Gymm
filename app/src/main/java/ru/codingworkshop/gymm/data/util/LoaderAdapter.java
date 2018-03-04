package ru.codingworkshop.gymm.data.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import io.reactivex.disposables.Disposable;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;

import static android.support.annotation.VisibleForTesting.PACKAGE_PRIVATE;

public class LoaderAdapter<T> {
    @VisibleForTesting(otherwise = PACKAGE_PRIVATE)
    @Nullable
    Disposable disposable;
    private T value;
    private Loader<T> loader;
    private MutableLiveData<T> liveData;

    public LoaderAdapter(@NonNull Loader<T> loader, @NonNull T value) {
        this.loader = loader;
        this.value = value;
    }

    public LiveData<T> load(long id) {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
            disposable = loader.loadById(value, id).subscribe(t -> {
                liveData.postValue(t);
                clear();
            });
        }

        return liveData;
    }

    public void clear() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
