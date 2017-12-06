package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.arch.lifecycle.LiveData;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramSet;

/**
 * Created by Radik on 09.11.2017.
 */

public interface ChildrenAdapter<C> {
    LiveData<List<C>> getChildren(long id);

    LiveData<List<Long>> insertChildren(Collection<C> children);

    void updateChildren(Collection<C> children);

    void deleteChildren(Collection<C> children);
}
