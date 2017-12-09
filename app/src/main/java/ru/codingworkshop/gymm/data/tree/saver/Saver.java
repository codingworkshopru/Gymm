package ru.codingworkshop.gymm.data.tree.saver;

import android.support.annotation.NonNull;

/**
 * Created by Radik on 28.11.2017.
 */

public interface Saver<T> {
    void save(@NonNull T objectToSave);
}
