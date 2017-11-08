package ru.codingworkshop.gymm.data.tree.holder;

import java.util.List;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public interface ChildrenHolder<C> {
    void setChildren(List<C> children);

    List<C> getChildren();

    void addChild(C child);

    void removeChild(C child);

    void removeChild(int index);

    void moveChild(int from, int to);

    void replaceChild(int index, C child);

    default boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    default int getChildrenCount() {
        return getChildren().size();
    }
}
