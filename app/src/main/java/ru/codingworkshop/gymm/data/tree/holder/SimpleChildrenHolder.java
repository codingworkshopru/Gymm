package ru.codingworkshop.gymm.data.tree.holder;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class SimpleChildrenHolder<C> extends ListChildrenHolder<C> {
    public SimpleChildrenHolder() {
        super();
    }

    public SimpleChildrenHolder(List<C> children) {
        super(children);
    }

    @Override
    List<C> createList() {
        return Lists.newArrayList();
    }

    @Override
    List<C> createList(List<C> children) {
        return Lists.newArrayList(children);
    }
}
