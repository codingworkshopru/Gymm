package ru.codingworkshop.gymm.data.tree.holder;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import ru.codingworkshop.gymm.data.tree.Preconditions2;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public class ImmutableChildrenHolder<C> extends ListChildrenHolder<C> {

    public ImmutableChildrenHolder() {
    }

    public ImmutableChildrenHolder(List<C> children) {
        super(children);
    }

    @Override
    List<C> createList() {
        return null;
    }

    @Override
    public void setChildren(@NonNull List<C> children) {
        Preconditions2.checkIsNull(getChildren(), "Trying to set children to immutable children holder");
        super.setChildren(children);
    }

    @Override
    List<C> createList(List<C> children) {
        return Collections.unmodifiableList(children);
    }
}
