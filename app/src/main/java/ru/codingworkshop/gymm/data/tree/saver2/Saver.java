package ru.codingworkshop.gymm.data.tree.saver2;

import android.support.annotation.NonNull;

/**
 * Created by Radik on 28.11.2017.
 */

public interface Saver<T> {
    void save(@NonNull T objectToSave);
}
