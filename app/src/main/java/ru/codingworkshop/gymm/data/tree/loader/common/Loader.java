package ru.codingworkshop.gymm.data.tree.loader.common;

import android.arch.lifecycle.LiveData;

/**
 * Created by Radik on 09.11.2017.
 */
public interface Loader<T> {
    LiveData<T> load(T node); // delete after rafactoring
    LiveData<T> loadById(T node, long id);
}
