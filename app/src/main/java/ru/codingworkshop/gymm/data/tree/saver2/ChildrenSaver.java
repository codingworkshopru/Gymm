package ru.codingworkshop.gymm.data.tree.saver2;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Equivalence;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ChildrenAdapter;
import ru.codingworkshop.gymm.data.util.BiPredicate;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;

/**
 * Created by Radik on 29.11.2017.
 */

public class ChildrenSaver<T extends Model> implements Saver<Collection<T>> {
    private ChildrenAdapter<T> adapter;
    private long parentId;
    private BiPredicate<T> areContentsTheSame;

    public ChildrenSaver(@NonNull ChildrenAdapter<T> adapter, long parentId, @NonNull BiPredicate<T> areContentsTheSame) {
        this.adapter = adapter;
        this.parentId = parentId;
        this.areContentsTheSame = areContentsTheSame;
    }

    @Override
    public void save(Collection<T> collection) {
        LiveDataUtil.getOnce(
                adapter.getChildren(parentId),
                oldChildren -> saveInternal(oldChildren, collection));
    }

    private void saveInternal(Collection<T> oldChildren, Collection<T> newChildren) {
        ListsDiffResult<T> result = containersDiff(oldChildren, newChildren, areContentsTheSame);
        adapter.insertChildren(result.getToInsert());
        adapter.updateChildren(result.getToUpdate());
        adapter.deleteChildren(result.getToDelete());
    }

    public static <T extends Model> ListsDiffResult<T> containersDiff(Collection<T> oldContainer, Collection<T> newContainer,
                                                                      @NonNull BiPredicate<T> areContentsTheSame)
    {
        ListsDiffResult<T> result = new ListsDiffResult<>();

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

            MapDifference<Long, T> difference = Maps.difference(oldListMap, newListMap, new Equivalence<T>() {
                protected boolean doEquivalent(@NonNull T a, @NonNull T b) {
                    return areContentsTheSame.test(a, b);
                }

                protected int doHash(@NonNull T t) {
                    return 0;
                }
            });

            result.setToDelete(difference.entriesOnlyOnLeft().values());
            result.setToInsert(difference.entriesOnlyOnRight().values());
            Collection<T> transformed = Collections2.transform(
                    difference.entriesDiffering().values(),
                    MapDifference.ValueDifference::rightValue);
            result.setToUpdate(transformed);
        }

        return result;
    }

    private static boolean emptyOrNull(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static class ListsDiffResult<T> {
        private Collection<T> toInsert;
        private Collection<T> toDelete;
        private Collection<T> toUpdate;

        public ListsDiffResult() {
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
