package ru.codingworkshop.gymm.data.tree.holder;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

public abstract class ListChildrenHolder<C> implements ChildrenHolder<C> {
    protected List<C> children;

    public ListChildrenHolder() {
        children = createList();
    }

    public ListChildrenHolder(List<C> children) {
        setChildren(children);
    }

    @Override
    public void setChildren(List<C> children) {
        this.children = createList(children);
    }

    abstract List<C> createList();

    abstract List<C> createList(List<C> children);

    @Override
    public List<C> getChildren() {
        return children;
    }

    @Override
    public void addChild(C child) {
        children.add(child);
    }

    @Override
    public void removeChild(C child) {
        boolean success = children.remove(child);
        Preconditions.checkArgument(success, "The child doesn't exist here");
        children.remove(child);
    }

    @Override
    public void removeChild(int index) {
        children.remove(index);
    }

    @Override
    public void moveChild(int from, int to) {
        Preconditions.checkElementIndex(from, children.size());
        Preconditions.checkElementIndex(to, children.size());
        children.add(to, children.remove(from));
    }
}
