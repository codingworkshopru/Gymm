package ru.codingworkshop.gymm.data.tree.loader.datasource;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

public abstract class NodeDataSource<P,C> {
    private LiveData<P> parent;
    private LiveData<List<C>> children;

    public LiveData<P> getParent() {
        return parent;
    }

    protected void setParent(@NonNull LiveData<P> parent) {
        if (this.parent == null) {
            this.parent = Preconditions.checkNotNull(parent);
        }
    }

    public LiveData<List<C>> getChildren() {
        return children;
    }

    protected void setChildren(@NonNull LiveData<List<C>> children) {
        if (this.children == null) {
            this.children = Preconditions.checkNotNull(children);
        }
    }
}
