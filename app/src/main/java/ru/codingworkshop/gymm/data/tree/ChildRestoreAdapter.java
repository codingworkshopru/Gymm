package ru.codingworkshop.gymm.data.tree;

import ru.codingworkshop.gymm.data.tree.holder.ChildrenHolder;
import ru.codingworkshop.gymm.data.tree.holder.SortableRestoreChildrenHolder;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class ChildRestoreAdapter implements ChildRestore {
    private BaseNode node;

    public ChildRestoreAdapter(BaseNode node) {
        this.node = node;
    }

    @Override
    public void restoreLastRemoved() {
        ChildrenHolder childrenHolder = node.getChildrenDelegate();
        if (childrenHolder instanceof SortableRestoreChildrenHolder) {
            ((SortableRestoreChildrenHolder) childrenHolder).restoreLastRemoved();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
