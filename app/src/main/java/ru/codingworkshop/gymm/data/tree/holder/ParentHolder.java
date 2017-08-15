package ru.codingworkshop.gymm.data.tree.holder;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public interface ParentHolder<P> {
    void setParent(P parent);
    P getParent();
}
