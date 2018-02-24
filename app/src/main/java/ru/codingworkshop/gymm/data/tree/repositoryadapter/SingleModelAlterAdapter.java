package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.common.Model;

public interface SingleModelAlterAdapter<M extends Model> {
    long insert(@NonNull M model);

    int update(@NonNull M model);

    int delete(@NonNull M model);
}
