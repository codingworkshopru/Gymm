package ru.codingworkshop.gymm.data.tree.loader2;

import io.reactivex.Flowable;

/**
 * Created by Radik on 19.12.2017.
 */

public interface Loader<T> {
    Flowable<T> loadById(T node, long id);
}
