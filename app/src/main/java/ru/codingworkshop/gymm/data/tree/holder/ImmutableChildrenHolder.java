package ru.codingworkshop.gymm.data.tree.holder;

import java.util.Collections;
import java.util.List;

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
    public void setChildren(List<C> children) {
        if (getChildren() == null || getChildren().isEmpty()) {
            super.setChildren(children);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    List<C> createList(List<C> children) {
        return Collections.unmodifiableList(children);
    }
}
