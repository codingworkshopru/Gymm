package ru.codingworkshop.gymm.data.entity.common;

import android.support.annotation.NonNull;

/**
 * Created by Радик on 26.04.2017.
 */

public interface Sortable extends Comparable<Sortable> {
    int getSortOrder();
    void setSortOrder(int sortOrder);

    @Override
    default int compareTo(@NonNull Sortable o) {
        return Integer.compare(getSortOrder(), o.getSortOrder());
    }
}
