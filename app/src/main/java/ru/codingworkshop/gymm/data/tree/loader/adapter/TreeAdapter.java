package ru.codingworkshop.gymm.data.tree.loader.adapter;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */
public abstract class TreeAdapter<P, C extends Model, GC> extends BaseNode<P, C> {
    private BaseNode<P, ? extends BaseNode<C, GC>> tree;
    private List<C> children;
    private Multimap<Long, GC> grandchildrenMultimap;

    public TreeAdapter(@NonNull BaseNode<P, ? extends BaseNode<C, GC>> tree) {
        super(null);
        this.tree = Preconditions.checkNotNull(tree);
    }

    public void build() {
        setChildrenNodes(buildChildrenNodes());
    }

    private List<BaseNode<C, GC>> buildChildrenNodes() {
        List<BaseNode<C, GC>> nodes = Lists.newArrayListWithCapacity(children.size());
        for (C child : children) {
            BaseNode<C, GC> node = createChildNode(child);
            node.setChildren(new ArrayList<>(grandchildrenMultimap.get(child.getId())));
            initNode(node);
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public void setParent(P parent) {
        tree.setParent(parent);
    }

    @Override
    public void setChildren(List<C> children) {
        this.children = children;
    }

    public void setGrandchildren(List<GC> grandchildren) {
        grandchildrenMultimap = Multimaps.index(grandchildren, this::getGrandchildParentId);
    }

    abstract void setChildrenNodes(List<BaseNode<C,GC>> childrenNodes);

    abstract void initNode(BaseNode<C,GC> node);

    abstract BaseNode<C,GC> createChildNode(C child);

    abstract long getGrandchildParentId(GC grandchild);
}
