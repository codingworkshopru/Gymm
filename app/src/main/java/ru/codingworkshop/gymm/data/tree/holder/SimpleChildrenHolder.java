package ru.codingworkshop.gymm.data.tree.holder;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class SimpleChildrenHolder<C> implements ChildrenHolder<C> {
    private List<C> children;

    @Override
    public void setChildren(List<C> children) {
        this.children = Lists.newArrayList(children);
    }

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
        children.remove(child);
    }

    @Override
    public void removeChild(int index) {
        children.remove(index);
    }

    @Override
    public void moveChild(int from, int to) {
        children.add(to, children.remove(from));
    }
}
