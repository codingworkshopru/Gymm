package ru.codingworkshop.gymm.repository;

import android.support.annotation.NonNull;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.common.Model;

@SuppressWarnings("Guava")
class InsertDelegate {
    @Inject
    InsertDelegate() {
    }

    <T extends Model> long insert(@NonNull T model, @NonNull Function<T, Long> insertFunction) {
        Long id = Preconditions.checkNotNull(insertFunction.apply(model), "insert function returned null");
        model.setId(id);
        return id;
    }

    <T extends Model> List<Long> insert(@NonNull Collection<T> models, @NonNull Function<Collection<T>, List<Long>> insertFunction) {
        List<Long> ids = Preconditions.checkNotNull(insertFunction.apply(models));

        Iterator<T> modelIterator = models.iterator();
        Iterator<Long> idIterator = ids.iterator();

        while(modelIterator.hasNext() && idIterator.hasNext()) {
            modelIterator.next().setId(idIterator.next());
        }

        return ids;
    }
}
