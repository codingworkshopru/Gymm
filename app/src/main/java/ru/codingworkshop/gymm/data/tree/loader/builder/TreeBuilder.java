package ru.codingworkshop.gymm.data.tree.loader.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

public abstract class TreeBuilder<P, C extends Model, GC> {
    private P parent;
    private List<C> children;
    private Multimap<Long, GC> grandchildren;

    private BaseNode<P, ? extends BaseNode<C, GC>> tree;

    public TreeBuilder(BaseNode<P, ? extends BaseNode<C, GC>> tree) {
        this.tree = tree;
    }

    public TreeBuilder<P, C, GC> setParent(P parent) {
        this.parent = parent;
        return this;
    }

    public TreeBuilder<P, C, GC> setChildren(List<C> children) {
        this.children = children;
        return this;
    }

    public TreeBuilder<P, C, GC> setGrandchildren(List<GC> grandchildren) {
        this.grandchildren = Multimaps.index(grandchildren, this::parentGetter);
        return this;
    }

    public BaseNode<P, ? extends BaseNode<C, GC>> build() {
        beforeBuild();
        tree.setParent(parent);

        List<BaseNode<C, GC>> nodes = Lists.newLinkedList();
        if (children != null) {
            for (C child : children) {
                BaseNode<C, GC> node = getNode(child);
                Collection<GC> gcn = grandchildren.get(child.getId());
                if (!gcn.isEmpty()) {
                    node.setChildren(new ArrayList<>(gcn));
                }
                nodes.add(node);
            }
        }

        setNodes(nodes);

        return tree;
    }

    abstract BaseNode<C, GC> getNode(C child);
    abstract void setNodes(List<? extends BaseNode<C, GC>> nodes);
    abstract void beforeBuild();
    abstract long parentGetter(GC grandchild);
}
