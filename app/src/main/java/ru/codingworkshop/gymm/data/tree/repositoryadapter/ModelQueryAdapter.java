package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

public interface ModelQueryAdapter<T> {
    @NonNull
    T query(long id);
}
