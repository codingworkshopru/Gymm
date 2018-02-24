package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.common.Model;

public interface ModelsAlterAdapter<M extends Model> {
    @NonNull
    List<Long> insert(@NonNull Collection<M> models);

    int update(@NonNull Collection<M> models);

    int delete(@NonNull Collection<M> models);
}
