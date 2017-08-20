package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.List;

import ru.codingworkshop.gymm.data.tree.node.BaseNode;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

public abstract class NodeLoader<P, C> {
    private BaseNode<P, C> node;
    private LiveData<P> parent;
    private LiveData<List<C>> children;

    public NodeLoader(@NonNull BaseNode<P, C> node) {
        Preconditions.checkNotNull(node, "Node must be not null");
        this.node = node;
    }

    public LiveData<Boolean> load() {
        Preconditions.checkNotNull(parent, "Live parent is not set");
        Preconditions.checkNotNull(children, "Live children is not set");

        SetAndRemove setAndRemove = new SetAndRemove();
        setAndRemove.ok(getParent(), node::setParent);
        setAndRemove.ok(getChildren(), node::setChildren);
        loadAdditional(setAndRemove);

        return setAndRemove.getLoaded();
    }

    public BaseNode<P, C> getNode() {
        return node;
    }

    void loadAdditional(SetAndRemove setAndRemove) {

    }

    public LiveData<P> getParent() {
        return parent;
    }

    public void setParent(@NonNull LiveData<P> parent) {
        this.parent = parent;
    }

    public LiveData<List<C>> getChildren() {
        return children;
    }

    public void setChildren(@NonNull LiveData<List<C>> children) {
        this.children = children;
    }
}
