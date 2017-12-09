package ru.codingworkshop.gymm.data.tree.saver;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ChildrenAdapter;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Radik on 29.11.2017.
 */

public class ChildrenSaver<T extends Model> implements Saver<Collection<T>> {
    private ChildrenAdapter<T> adapter;
    private long parentId;

    public ChildrenSaver(@NonNull ChildrenAdapter<T> adapter, long parentId) {
        this.adapter = adapter;
        this.parentId = parentId;
    }

    @Override
    public void save(@NonNull Collection<T> collection) {
        Preconditions.checkState(GymmDatabase.isValidId(parentId), "parent id is not valid");
        LiveDataUtil.getOnce(
                adapter.getChildren(parentId),
                oldChildren -> saveInternal(oldChildren, collection));
    }

    private void saveInternal(Collection<T> oldChildren, Collection<T> newChildren) {
        ContainersDiffResult<T> result = containersDiff(oldChildren, newChildren);
        adapter.insertChildren(result.getToInsert());
        adapter.updateChildren(result.getToUpdate());
        adapter.deleteChildren(result.getToDelete());
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
            Map<Long, T> oldListMap = Maps.uniqueIndex(oldContainer, Model::getId);
            Map<Long, T> newListMap = Maps.uniqueIndex(newContainer, Model::getId);

            MapDifference<Long, T> difference = Maps.difference(oldListMap, newListMap);

            result.setToDelete(difference.entriesOnlyOnLeft().values());
            result.setToInsert(difference.entriesOnlyOnRight().values());
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
        private Collection<T> toInsert;
        private Collection<T> toDelete;
        private Collection<T> toUpdate;

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
