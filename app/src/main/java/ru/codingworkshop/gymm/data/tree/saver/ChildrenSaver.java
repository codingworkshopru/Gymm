package ru.codingworkshop.gymm.data.tree.saver;

import java.util.Collection;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public interface ChildrenSaver<C> extends Saver {

    boolean objectsAreSame(C child1, C child2);
    boolean contentsAreEqual(C child1, C child2);

    void insertChildren(Collection<C> children);
    void updateChildren(Collection<C> children);
    void deleteChildren(Collection<C> children);
}
