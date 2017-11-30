package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.arch.lifecycle.LiveData;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramSet;

/**
 * Created by Radik on 13.11.2017.
 */

public interface GrandchildrenAdapter<GC> {
    LiveData<List<GC>> getGrandchildren(long id);

    void insertGrandchildren(Collection<GC> grandchildren);
    void updateGrandchildren(Collection<GC> grandchildren);
    void deleteGrandchildren(Collection<GC> grandchildren);
}
