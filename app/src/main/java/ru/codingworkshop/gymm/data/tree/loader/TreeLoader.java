package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.holder.SimpleChildrenHolder;
import ru.codingworkshop.gymm.data.tree.loader.builder.TreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.datasource.TreeDataSource;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

public class TreeLoader<P,C extends Model,GC> extends NodeLoader<P,C> {
    private TreeBuilder<P, C, GC> builder;
    private TreeDataSource<P,C,GC> dataSource;
    private LiveData<Boolean> loaded;

    public TreeLoader(@NonNull TreeDataSource<P,C,GC> dataSource, @NonNull TreeBuilder<P,C,GC> builder) {
        super(new BaseNode<P, C>(new SimpleChildrenHolder<>()) {}, dataSource);
        this.dataSource = Preconditions.checkNotNull(dataSource);
        this.builder = builder;
    }

    @Override
    public LiveData<Boolean> load() {
        loaded = super.load();
        loaded.observeForever(this::buildChildNodes);
        return loaded;
    }

    public TreeBuilder<P, C, GC> getBuilder() {
        return builder;
    }

    private void buildChildNodes(boolean isLoaded) {
        if (!isLoaded) return;
        loaded.removeObserver(this::buildChildNodes);

        builder.setParent(getNode().getParent());
        builder.setChildren(getNode().getChildren());

        builder.build();
    }

    @Override
    void loadAdditional(SetAndRemove setAndRemove) {
        setAndRemove.ok(dataSource.getGrandchildren(), builder::setGrandchildren);
    }
}
