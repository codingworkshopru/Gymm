package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.arch.lifecycle.LiveData;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;

/**
 * Created by Radik on 09.11.2017.
 */

public interface ParentAdapter<P> {
    LiveData<P> getParent(long id);

    void updateParent(P item);

    void insertParent(P item);

    void deleteParent(P item);
}
