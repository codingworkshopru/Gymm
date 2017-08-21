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

public class SortableRestoreChildrenHolder<C extends Sortable> extends ListChildrenHolder<C> implements ChildRestore {
    private C lastRemoved;

    public SortableRestoreChildrenHolder() {
        super();
    }

    public SortableRestoreChildrenHolder(List<C> children) {
        super(children);
    }

    @Override
    List<C> createList() {
        return Lists.newLinkedList();
    }

    @Override
    public List<C> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void setChildren(List<C> children) {
        super.setChildren(children);
        Collections.sort(this.children, (a, b) -> a.getSortOrder() - b.getSortOrder());
    }

    @Override
    List<C> createList(List<C> children) {
        return Lists.newLinkedList(children);
    }

    @Override
    public void addChild(C child) {
        child.setSortOrder(children.size());
        super.addChild(child);
    }

    @Override
    public void removeChild(C child) {
        super.removeChild(child);
        lastRemoved = child;
        updateSortOrders(child.getSortOrder());
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
        super.moveChild(from, to);
        updateSortOrders(Math.min(from, to), Math.max(from, to));
    }

    @Override
    public void restoreLastRemoved() {
        insert(lastRemoved.getSortOrder(), lastRemoved);
    }
}
