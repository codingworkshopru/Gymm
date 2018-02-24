package ru.codingworkshop.gymm.data.tree.saver;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ModelQueryAdapter;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ModelsAlterAdapter;
import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Radik on 29.11.2017.
 */

class ChildrenSaver<T extends Model> implements Saver<Collection<T>> {
    private ModelQueryAdapter<List<T>> querySource;
    private ModelsAlterAdapter<T> alterSource;
    private long parentId;

    ChildrenSaver(@NonNull ModelQueryAdapter<List<T>> querySource, @NonNull ModelsAlterAdapter<T> alterSource, long parentId) {
        Preconditions.checkArgument(GymmDatabase.isValidId(parentId));
        this.querySource = querySource;
        this.alterSource = alterSource;
        this.parentId = parentId;
    }

    @Override
    public Completable save(@NonNull Collection<T> objectToSave) {
        ContainersDiffResult<T> result = containersDiff(querySource.query(parentId), objectToSave);
        alterSource.insert(result.getToInsert());
        alterSource.update(result.getToUpdate());
        alterSource.delete(result.getToDelete());

        return null;
    }

    public static <T extends Model> ContainersDiffResult<T> containersDiff(Collection<T> oldContainer, Collection<T> newContainer)
    {
        ContainersDiffResult<T> result = new ContainersDiffResult<>();

        boolean emptyOld = emptyOrNull(oldContainer);
        boolean emptyNew = emptyOrNull(newContainer);

        if (emptyOld && emptyNew) {
            return result;
        } else if (emptyOld) {
            result.setToInsert(newContainer);
        } else if (emptyNew) {
            result.setToDelete(oldContainer);
        } else {
            List<T> toInsert = new ArrayList<>(Collections2.filter(newContainer, model -> !GymmDatabase.isValidId(model)));
            Map<Long, T> oldListMap = Maps.uniqueIndex(oldContainer, Model::getId);
            Map<Long, T> newListMap = Maps.uniqueIndex(Collections2.filter(newContainer, GymmDatabase::isValidId), Model::getId);

            MapDifference<Long, T> difference = Maps.difference(oldListMap, newListMap);

            result.setToDelete(difference.entriesOnlyOnLeft().values());

            toInsert.addAll(difference.entriesOnlyOnRight().values());
            result.setToInsert(toInsert);

            Collection<T> changedItems = Collections2.transform(
                    difference.entriesDiffering().values(),
                    MapDifference.ValueDifference::rightValue);
            result.setToUpdate(changedItems);
        }

        return result;
    }

    private static boolean emptyOrNull(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static class ContainersDiffResult<T> {
        private Collection<T> toInsert = Collections.emptyList();
        private Collection<T> toDelete = Collections.emptyList();
        private Collection<T> toUpdate = Collections.emptyList();

        public ContainersDiffResult() {
        }

        public Collection<T> getToInsert() {
            return toInsert;
        }

        void setToInsert(Collection<T> toInsert) {
            this.toInsert = toInsert;
        }

        public Collection<T> getToDelete() {
            return toDelete;
        }

        void setToDelete(Collection<T> toDelete) {
            this.toDelete = toDelete;
        }

        public Collection<T> getToUpdate() {
            return toUpdate;
        }

        void setToUpdate(Collection<T> toUpdate) {
            this.toUpdate = toUpdate;
        }
    }
}
