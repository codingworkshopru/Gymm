package ru.codingworkshop.gymm.data.wrapper;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.common.Sortable;

/**
 * Created by Радик on 03.08.2017 as part of the Gymm project.
 */

public abstract class BaseWrapper<P, C extends Sortable> {
    private P root;
    private SortableChildrenDelegate<C> childrenDelegate = new SortableChildrenDelegate<>();

    public P getRoot() {
        return root;
    }

    public void setRoot(@NonNull P root) {
        this.root = root;
    }

    public List<C> getChildren() {
        return childrenDelegate.getChildren();
    }

    public void setChildren(@NonNull Collection<C> children) {
        childrenDelegate.setChildren(children);
    }

    public boolean hasChildren() {
        return childrenDelegate.hasChildren();
    }

    public void addChild(@NonNull C child) {
        childrenDelegate.add(child);
    }

    public void removeChild(@NonNull C child) {
        childrenDelegate.remove(child);
    }

    public void removeChild(int index) {
        childrenDelegate.remove(index);
    }

    public void move(int from, int to) {
        childrenDelegate.move(from, to);
    }

    public void restoreLastRemoved() {
        childrenDelegate.restoreLastRemoved();
    }

    public void save() {
        Preconditions.checkState(hasChildren(), "Must have children");
        saveRoot();
        saveChildren();
    }

    protected abstract void saveRoot();
    protected abstract void saveChildren();
}
