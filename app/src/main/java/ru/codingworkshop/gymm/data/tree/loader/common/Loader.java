package ru.codingworkshop.gymm.data.tree.loader.common;

import io.reactivex.Flowable;

/**
 * Created by Radik on 09.11.2017.
 */
public interface Loader<T> {
    Flowable<T> loadById(T node, long id);
}
