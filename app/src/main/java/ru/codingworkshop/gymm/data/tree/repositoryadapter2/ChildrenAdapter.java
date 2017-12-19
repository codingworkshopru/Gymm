package ru.codingworkshop.gymm.data.tree.repositoryadapter2;

import java.util.Collection;
import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Radik on 17.12.2017.
 */

public interface ChildrenAdapter<T> {
    Flowable<List<T>> getChildren(long parentId);

    List<Long> insertChildren(Collection<T> children);

    void updateChildren(Collection<T> children);

    void deleteChildren(Collection<T> children);
}
