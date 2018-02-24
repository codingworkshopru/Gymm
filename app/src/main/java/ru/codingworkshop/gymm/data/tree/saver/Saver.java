package ru.codingworkshop.gymm.data.tree.saver;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Completable;

/**
 * Created by Radik on 28.11.2017.
 */

public interface Saver<T> {
    @Nullable
    Completable save(@NonNull T objectToSave);
}
