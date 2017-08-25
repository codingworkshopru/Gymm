package ru.codingworkshop.gymm.data.tree.saver;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Радик on 17.08.2017 as part of the Gymm project.
 */

public class ChildrenSaver<C> implements Saver {
    private Collection<C> children;
    private LiveData<List<C>> oldChildrenLiveData;
    private ChildrenSaverCallback<C> callback;

    public interface ChildrenSaverCallback<C> {
        boolean objectsAreSame(C child1, C child2);
        boolean contentsAreEqual(C child1, C child2);

        void insertChildren(Collection<C> children);
        void updateChildren(Collection<C> children);
        void deleteChildren(Collection<C> children);
    }

    public ChildrenSaver(@NonNull Collection<C> children, @NonNull LiveData<List<C>> oldChildrenLiveData, @NonNull ChildrenSaverCallback<C> callback) {
        this.children = Preconditions.checkNotNull(children);
        this.callback = Preconditions.checkNotNull(callback);
        this.oldChildrenLiveData = Preconditions.checkNotNull(oldChildrenLiveData);
    }

    @Override
    public void save() {
        oldChildrenLiveData.observeForever(this::difference);
    }

//    private void difference(Collection<C> oldChildren) {
//        oldChildrenLiveData.removeObserver(this::difference);
//
//        if (oldChildren == null) return;
//
//        if (oldChildren.isEmpty()) {
//            insert(children);
//            return;
//        }
//
//        if (children.isEmpty()) {
//            delete(oldChildren);
//            Timber.w("All children about to be deleted");
//            return;
//        }
//
//        Collection<C> toInsert = sub(children, oldChildren);
//        Collection<C> toDelete = sub(oldChildren, children);
//        Collection<C> toUpdate = Collections2.filter(children, input -> {
//            if (toInsert.contains(input)) {
//                return false;
//            }
//            return ! Iterables.tryFind(oldChildren, old ->
//                    callback.contentsAreEqual(input, old)
//            ).isPresent();
//        });
//
//        insert(toInsert);
//        update(toUpdate);
//        delete(toDelete);
//    }

    private void difference(List<C> oldChildren) {
        oldChildrenLiveData.removeObserver(this::difference);

        if (oldChildren == null || children == null) return;

        if (oldChildren.isEmpty()) {
            insert(children);
            return;
        }

        if (children.isEmpty()) {
            delete(oldChildren);
            Timber.w("All children about to be deleted");
            return;
        }

        List<C> toDelete = Lists.newArrayList();
        List<C> toUpdate = Lists.newArrayList();
        List<C> toInsert = Lists.newArrayList();

        for (C oldChild : oldChildren) {
            if (Collections2.filter(children, newChild -> callback.objectsAreSame(oldChild, newChild)).isEmpty()) {
                toDelete.add(oldChild);
            }
        }

        for (C newChild : children) {
            C found = Iterables.tryFind(oldChildren, oldChild -> callback.objectsAreSame(oldChild, newChild)).orNull();
            if (found == null) {
                toInsert.add(newChild);
            } else if (!callback.contentsAreEqual(found, newChild)) {
                toUpdate.add(newChild);
            }
        }

        delete(toDelete);
        update(toUpdate);
        insert(toInsert);

    }

    private void insert(Collection<C> toInsert) {
        if (!toInsert.isEmpty()) {
            callback.insertChildren(toInsert);
        }
    }

    private void update(Collection<C> toUpdate) {
        if (!toUpdate.isEmpty()) {
            callback.updateChildren(toUpdate);
        }
    }

    private void delete(Collection<C> toDelete) {
        if (!toDelete.isEmpty()) {
            callback.deleteChildren(toDelete);
        }
    }
}
