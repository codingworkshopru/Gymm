package ru.codingworkshop.gymm.data.tree.repositoryadapter2;

import io.reactivex.Single;

/**
 * Created by Radik on 17.12.2017.
 */

public interface ParentAdapter<T> {
    Single<T> getParent(long parentId);

    long insertParent(T parent);

    void updateParent(T parent);

    void deleteParent(T parent);
}
