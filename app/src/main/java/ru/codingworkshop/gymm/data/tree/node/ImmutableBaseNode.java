package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.tree.Preconditions2;
import ru.codingworkshop.gymm.data.tree.holder.ImmutableChildrenHolder;

public class ImmutableBaseNode<P, C> extends BaseNode<P, C> {
    public ImmutableBaseNode() {
        super(new ImmutableChildrenHolder<>());
    }

    @Override
    public void setParent(P parent) {
        Preconditions2.checkIsNull(getParent(), "Immutable tree already has a parent");
        super.setParent(parent);
    }
}
