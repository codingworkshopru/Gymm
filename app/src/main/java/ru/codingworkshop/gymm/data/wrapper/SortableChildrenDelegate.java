package ru.codingworkshop.gymm.data.wrapper;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.common.Sortable;

/**
 * Created by Радик on 13.07.2017.
 */

public class SortableChildrenDelegate<C extends Sortable> {
    private List<C> children;
    private C lastRemoved;

    public SortableChildrenDelegate() {
        children = Lists.newLinkedList();
    }

    public SortableChildrenDelegate(@NonNull Collection<C> source) {
        setChildren(source);
    }

    public void add(@NonNull C child) {
        child.setSortOrder(children.size());
        children.add(child);
    }

    public void remove(int index) {
        Preconditions.checkElementIndex(index, children.size());
        remove(children.get(index));
    }

    public void remove(@NonNull C child) {
        Preconditions.checkArgument(children.remove(child), "The child doesn't exists here");
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

    public void setChildren(@NonNull Collection<? extends C> children) {
        this.children = Lists.newLinkedList(children);
        Collections.sort(this.children, (a, b) -> a.getSortOrder() - b.getSortOrder());
    }

    public List<C> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void restoreLastRemoved() {
        insert(lastRemoved.getSortOrder(), lastRemoved);
    }

    public void move(int from, int to) {
        Preconditions.checkElementIndex(from, children.size());
        Preconditions.checkElementIndex(to, children.size());
        C child = children.remove(from);
        children.add(to, child);
        updateSortOrders(Math.min(from, to), Math.max(from, to));
    }
}
