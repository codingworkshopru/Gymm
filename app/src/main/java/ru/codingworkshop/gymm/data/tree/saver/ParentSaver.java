package ru.codingworkshop.gymm.data.tree.saver;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public interface ParentSaver<P> extends Saver {
    void updateParent(P parent);
    void insertParent(P parent);
}
