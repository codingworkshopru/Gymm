package ru.codingworkshop.gymm.data.tree.saver;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Радик on 17.08.2017 as part of the Gymm project.
 */

abstract class AbstractChildrenSaver<C extends Model> implements Observer<List<C>>, ChildrenSaver<C> {
    private Collection<C> children;
    private LiveData<List<C>> oldChildrenLive;

    public AbstractChildrenSaver(@NonNull Collection<C> children, @NonNull LiveData<List<C>> oldChildrenLive) {
        this.children = Preconditions.checkNotNull(children);
        this.oldChildrenLive = Preconditions.checkNotNull(oldChildrenLive);
    }

    @Override
    public void save() {
        oldChildrenLive.observeForever(this);
    }

    @Override
    public void onChanged(@Nullable List<C> oldChildren) {
        if (oldChildren == null) return;

        if (oldChildren.isEmpty()) {
            insertChildren(children);
            return;
        }

        Collection<C> toInsert = Lists.newLinkedList();
        Collection<C> toUpdate = Lists.newLinkedList();
        Collection<C> toDelete = Collections2.filter(oldChildren, oldChild ->
                !Iterables.tryFind(children, newChild -> objectsAreSame(oldChild, newChild)).isPresent()
        );

        Map<Long, C> oldMap = Maps.uniqueIndex(oldChildren, Model::getId);

        for (C newChild : children) {
            if (!GymmDatabase.isValidId(newChild)) {
                toInsert.add(newChild);
            } else if (!contentsAreEqual(newChild, oldMap.get(newChild.getId()))) {
                toUpdate.add(newChild);
            }
        }

        if (!toInsert.isEmpty()) {
            insertChildren(toInsert);
        }

        if (!toUpdate.isEmpty()) {
            updateChildren(toUpdate);
        }

        if (!toDelete.isEmpty()) {
            deleteChildren(toDelete);
        }
    }
}
