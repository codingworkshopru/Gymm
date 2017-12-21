package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import io.reactivex.Flowable;

/**
 * Created by Radik on 09.11.2017.
 */

public interface ParentAdapter<P> {
    Flowable<P> getParent(long id);

    void updateParent(P item);

    long insertParent(P item);

    void deleteParent(P item);
}
