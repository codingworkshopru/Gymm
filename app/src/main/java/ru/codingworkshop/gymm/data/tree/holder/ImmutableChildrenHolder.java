package ru.codingworkshop.gymm.data.tree.holder;

import java.util.Collections;
import java.util.List;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public class ImmutableChildrenHolder<C> implements ChildrenHolder<C> {
    private List<C> children;

    public ImmutableChildrenHolder() {
    }

    public ImmutableChildrenHolder(List<C> children) {
        setChildren(children);
    }

    @Override
    public void setChildren(List<C> children) {
        if (this.children == null || this.children.isEmpty()) {
            this.children = Collections.unmodifiableList(children);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public List<C> getChildren() {
        return children;
    }

    @Override
    public void addChild(C child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeChild(C child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeChild(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveChild(int from, int to) {
        throw new UnsupportedOperationException();
    }
}
