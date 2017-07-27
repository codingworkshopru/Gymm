package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.entity.common.Sortable;

/**
 * Created by Радик on 27.07.2017.
 */

public abstract class ChildrenSaver<T extends Model & Sortable> implements Observer<List<T>> {
    private LiveData<List<T>> oldChildrenLive;
    private List<T> newChildren;

    public ChildrenSaver(LiveData<List<T>> oldChildrenLive, List<T> newChildren) {
        this.oldChildrenLive = oldChildrenLive;
        this.newChildren = newChildren;
    }

    public void save() {
        oldChildrenLive.observeForever(this);
    }

    private void difference(List<T> oldChildren) {
        if (oldChildren != null && newChildren != null) {
            List<T> toDelete = Lists.newArrayList();
            List<T> toUpdate = Lists.newArrayList();
            List<T> toInsert = Lists.newArrayList();

            for (T oldChild : oldChildren) {
                if (Collections2.filter(newChildren, newChild -> elementsAreSame(oldChild, newChild)).isEmpty()) {
                    toDelete.add(oldChild);
                }
            }

            for (T newChild : newChildren) {
                Collection<T> theSameOldChild = Collections2.filter(oldChildren, oldChild -> elementsAreSame(oldChild, newChild));
                if (theSameOldChild.isEmpty()) {
                    toInsert.add(newChild);
                } else if (!elementsAreEqual(Iterables.getFirst(theSameOldChild, null), newChild)) {
                    toUpdate.add(newChild);
                }
            }

            delete(toDelete);
            update(toUpdate);
            insert(toInsert);
        }
    }

    @Override
    public void onChanged(@Nullable List<T> oldChildren) {
        difference(oldChildren);
        oldChildrenLive.removeObserver(this);
    }

    protected boolean elementsAreSame(T element1, T element2) {
        return element1.getId() == element2.getId();
    }

    protected boolean elementsAreEqual(T element1, T element2) {
        return element1.getSortOrder() == element2.getSortOrder();
    }

    public abstract void update(List<T> toUpdate);
    public abstract void delete(List<T> toDelete);
    public abstract void insert(List<T> toInsert);
}
