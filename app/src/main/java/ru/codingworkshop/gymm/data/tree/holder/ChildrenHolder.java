package ru.codingworkshop.gymm.data.tree.holder;

import java.util.List;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public interface ChildrenHolder<C> {
    void setChildren(List<? extends C> children);

    List<C> getChildren();

    void addChild(C child);

    void removeChild(C child);

    void removeChild(int index);

    void moveChild(int from, int to);

    default boolean hasChildren() {
        return !getChildren().isEmpty();
    }
}
