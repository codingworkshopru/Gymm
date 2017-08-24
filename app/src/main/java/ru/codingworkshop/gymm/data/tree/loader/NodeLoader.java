package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import ru.codingworkshop.gymm.data.tree.loader.datasource.NodeDataSource;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

public abstract class NodeLoader<P, C> {
    private BaseNode<P, C> node;
    private NodeDataSource<P, C> dataSource;

    public NodeLoader(@NonNull BaseNode<P, C> node, @NonNull NodeDataSource<P, C> dataSource) {
        this.node = Preconditions.checkNotNull(node, "Node must be not null");
        this.dataSource = Preconditions.checkNotNull(dataSource, "Data source must be not null");
    }

    public LiveData<Boolean> load() {
        SetAndRemove setAndRemove = new SetAndRemove();
        setAndRemove.ok(dataSource.getParent(), node::setParent);
        setAndRemove.ok(dataSource.getChildren(), node::setChildren);
        loadAdditional(setAndRemove);

        return setAndRemove.getLoaded();
    }

    public BaseNode<P, C> getNode() {
        return node;
    }

    abstract void loadAdditional(SetAndRemove setAndRemove);
}
