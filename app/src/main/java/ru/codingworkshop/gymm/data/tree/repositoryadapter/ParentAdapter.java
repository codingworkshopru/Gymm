package ru.codingworkshop.gymm.data.tree.repositoryadapter;

/**
 * Created by Radik on 09.11.2017.
 */

public interface ParentAdapter<P> {
    P getParent(long id);

    void updateParent(P item);

    long insertParent(P item);

    void deleteParent(P item);
}
