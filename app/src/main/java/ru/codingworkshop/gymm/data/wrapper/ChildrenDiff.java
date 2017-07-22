package ru.codingworkshop.gymm.data.wrapper;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Радик on 11.07.2017.
 */

public abstract class ChildrenDiff<T> {
    protected List<T> oldChildren;
    private List<T> newChildren;
    private Comparator<T> elementsAreSame;
    private Comparator<T> contentsAreEqual;

    public ChildrenDiff(List<T> oldChildren, List<T> newChildren, Comparator<T> elementsAreSame, Comparator<T> contentsAreEqual) {
        this.oldChildren = oldChildren;
        this.newChildren = newChildren;
        this.elementsAreSame = elementsAreSame;
        this.contentsAreEqual = contentsAreEqual;
    }

    public void calculate() {
        if (oldChildren == null || newChildren == null)
            return;

        List<T> toDelete = Lists.newArrayList();
        List<T> toUpdate = Lists.newArrayList();
        List<T> toInsert = Lists.newArrayList();

        for (T oldChild : oldChildren) {
            if (Collections2.filter(newChildren, newChild -> elementsAreSame.compare(oldChild, newChild) == 0).isEmpty()) {
                toDelete.add(oldChild);
            }
        }

        for (T newChild : newChildren) {
            Collection<T> theSameOldChild = Collections2.filter(oldChildren, oldChild -> elementsAreSame.compare(oldChild, newChild) == 0);
            if (theSameOldChild.isEmpty()) {
                toInsert.add(newChild);
            } else if (contentsAreEqual.compare(Iterables.getFirst(theSameOldChild, null), newChild) != 0) {
                toUpdate.add(newChild);
            }
        }

        difference(toDelete, toUpdate, toInsert);
    }

    public abstract void difference(List<T> toDelete, List<T> toUpdate, List<T> toInsert);
}
