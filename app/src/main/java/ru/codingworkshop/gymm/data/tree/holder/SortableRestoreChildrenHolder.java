package ru.codingworkshop.gymm.data.tree.holder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.common.Sortable;
import ru.codingworkshop.gymm.data.tree.ChildRestore;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public class SortableRestoreChildrenHolder<C extends Sortable> implements ChildrenHolder<C>, ChildRestore {
    private List<C> children;
    private C lastRemoved;

    public SortableRestoreChildrenHolder() {
        children = Lists.newArrayList();
    }

    public SortableRestoreChildrenHolder(List<C> children) {
        this.children = children;
    }

    @Override
    public List<C> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void setChildren(List<? extends C> children) {
        this.children = Lists.newLinkedList(children);
        Collections.sort(this.children, (a, b) -> a.getSortOrder() - b.getSortOrder());
    }

    @Override
    public void addChild(C child) {
        child.setSortOrder(children.size());
        children.add(child);
    }

    @Override
    public void removeChild(C child) {
        Preconditions.checkArgument(children.remove(child), "The child doesn't exists here");
        lastRemoved = child;
        updateSortOrders(child.getSortOrder());
        children.remove(child);
    }

    private void updateSortOrders(int start) {
        updateSortOrders(start, children.size() - 1);
    }

    private void updateSortOrders(int start, int end) {
        for (int i = start; i <= end; i++) {
            children.get(i).setSortOrder(i);
        }
    }

    private void insert(int position, C element) {
        children.add(position, element);
        updateSortOrders(position + 1);
    }

    @Override
    public void removeChild(int index) {
        Preconditions.checkElementIndex(index, children.size());
        removeChild(children.get(index));
    }

    @Override
    public void moveChild(int from, int to) {
        Preconditions.checkElementIndex(from, children.size());
        Preconditions.checkElementIndex(to, children.size());
        C child = children.remove(from);
        children.add(to, child);
        updateSortOrders(Math.min(from, to), Math.max(from, to));
    }

    @Override
    public void restoreLastRemoved() {
        insert(lastRemoved.getSortOrder(), lastRemoved);
    }
}
