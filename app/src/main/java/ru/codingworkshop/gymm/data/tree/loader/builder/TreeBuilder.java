package ru.codingworkshop.gymm.data.tree.loader.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.holder.ChildrenHolder;
import ru.codingworkshop.gymm.data.tree.holder.ParentHolder;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

public abstract class TreeBuilder<P, C extends Model, GC> implements ParentHolder<P>, ChildrenHolder<C> {
    private P parent;
    private List<C> children;
    private Multimap<Long, GC> grandchildren;

    private BaseNode<P, ? extends BaseNode<C, GC>> tree;

    public TreeBuilder(BaseNode<P, ? extends BaseNode<C, GC>> tree) {
        this.tree = tree;
    }

    @Override
    public void setParent(P parent) {
        this.parent = parent;
    }

    @Override
    public P getParent() {
        return parent;
    }

    @Override
    public void setChildren(List<C> children) {
        this.children = children;
    }

    @Override
    public List<C> getChildren() {
        return children;
    }

    @Override
    public void addChild(C child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeChild(C child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeChild(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveChild(int from, int to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceChild(int index, C child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasChildren() {
        return children.isEmpty();
    }

    @Override
    public int getChildrenCount() {
        return children.size();
    }

    public void setGrandchildren(List<GC> grandchildren) {
        this.grandchildren = Multimaps.index(grandchildren, this::parentGetter);
    }

    public BaseNode<P, ? extends BaseNode<C, GC>> build() {
        beforeBuild();
        tree.setParent(parent);

        List<BaseNode<C, GC>> nodes = Lists.newLinkedList();
        if (children != null) {
            for (C child : children) {
                BaseNode<C, GC> node = getNode(child);
                node.setChildren(new ArrayList<>(grandchildren.get(child.getId())));
                nodes.add(node);
            }
        }

        setNodes(nodes);

        return tree;
    }

    protected void beforeBuild() {

    }

    abstract protected BaseNode<C, GC> getNode(C child);
    abstract protected void setNodes(List<? extends BaseNode<C, GC>> nodes);
    abstract protected long parentGetter(GC grandchild);
}
