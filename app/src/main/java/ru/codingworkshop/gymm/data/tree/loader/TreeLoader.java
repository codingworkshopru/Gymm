package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.common.Model;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

public class TreeLoader<P,C extends Model,GC> extends NodeLoader<P,C> {
    private TreeAdapter<P,C,GC> adapter;
    private LiveData<List<GC>> grandchildren;
    private LiveData<Boolean> loaded;

    public TreeLoader(@NonNull TreeAdapter<P,C,GC> tree) {
        super(tree);
        this.adapter = Preconditions.checkNotNull(tree);
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
        setAndRemove.ok(grandchildren, adapter::setGrandchildren);
    }

    public void setGrandchildren(@NonNull LiveData<List<GC>> grandchildren) {
        this.grandchildren = Preconditions.checkNotNull(grandchildren);
    }
}
