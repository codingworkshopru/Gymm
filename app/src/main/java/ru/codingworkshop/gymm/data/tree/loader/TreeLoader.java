package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.loader.adapter.TreeAdapter;
import ru.codingworkshop.gymm.data.tree.loader.datasource.TreeDataSource;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

public class TreeLoader<P,C extends Model,GC> extends NodeLoader<P,C> {
    private TreeAdapter<P,C,GC> adapter;
    private TreeDataSource<P,C,GC> dataSource;
    private LiveData<Boolean> loaded;

    public TreeLoader(@NonNull TreeAdapter<P,C,GC> tree, @NonNull TreeDataSource<P,C,GC> dataSource) {
        super(tree, dataSource);
        this.adapter = Preconditions.checkNotNull(tree);
        this.dataSource = Preconditions.checkNotNull(dataSource);
    }

    @Override
    public LiveData<Boolean> load() {
        loaded = super.load();
        loaded.observeForever(this::buildChildNodes);
        return loaded;
    }

    private void buildChildNodes(boolean isLoaded) {
        if (!isLoaded) return;
        loaded.removeObserver(this::buildChildNodes);
        adapter.build();
    }

    @Override
    void loadAdditional(SetAndRemove setAndRemove) {
        setAndRemove.ok(dataSource.getGrandchildren(), adapter::setGrandchildren);
    }
}
