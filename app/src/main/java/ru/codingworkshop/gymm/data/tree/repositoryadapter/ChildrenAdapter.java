package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import java.util.Collection;
import java.util.List;

/**
 * Created by Radik on 09.11.2017.
 */

public interface ChildrenAdapter<C> {
    List<C> getChildren(long id);

    List<Long> insertChildren(Collection<C> children);

    void updateChildren(Collection<C> children);

    void deleteChildren(Collection<C> children);
}
