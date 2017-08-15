package ru.codingworkshop.gymm.data.tree.node;

import java.util.List;

import ru.codingworkshop.gymm.data.tree.holder.ChildrenHolder;
import ru.codingworkshop.gymm.data.tree.holder.ParentHolder;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

abstract class BaseNode<P,C> implements ParentHolder<P>, ChildrenHolder<C> {
    private P parent;
    private ChildrenHolder<C> childrenDelegate;

    public BaseNode(ChildrenHolder<C> childrenDelegate) {
        this.childrenDelegate = childrenDelegate;
    }

    protected ChildrenHolder<C> getChildrenDelegate() {
        return childrenDelegate;
    }

    @Override
    public P getParent() {
        return parent;
    }

    @Override
    public void setParent(P parent) {
        this.parent = parent;
    }

    @Override
    public List<C> getChildren() {
        return childrenDelegate.getChildren();
    }

    @Override
    public void setChildren(List<? extends C> children) {
        this.childrenDelegate.setChildren(children);
    }

    @Override
    public void addChild(C child) {
        childrenDelegate.addChild(child);
    }

    @Override
    public void removeChild(C child) {
        childrenDelegate.removeChild(child);
    }

    @Override
    public void removeChild(int index) {
        childrenDelegate.removeChild(index);
    }

    @Override
    public void moveChild(int from, int to) {
        childrenDelegate.moveChild(from, to);
    }
}
