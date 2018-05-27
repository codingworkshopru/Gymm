package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import java.util.Collection;
import java.util.List;

/**
 * Created by Radik on 13.11.2017.
 */

public interface GrandchildrenAdapter<GC> {
    List<GC> getGrandchildren(long id);

    void insertGrandchildren(Collection<GC> grandchildren);
    void updateGrandchildren(Collection<GC> grandchildren);
    void deleteGrandchildren(Collection<GC> grandchildren);
}
