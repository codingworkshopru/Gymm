package ru.codingworkshop.gymm.data.tree.loader.datasource;

import android.arch.lifecycle.LiveData;

import java.util.List;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

public class TreeDataSource<P,C,GC> extends NodeDataSource<P,C> {
    private LiveData<List<GC>> grandchildren;

    void setGrandchildren(LiveData<List<GC>> grandchildren) {
        this.grandchildren = grandchildren;
    }

    public LiveData<List<GC>> getGrandchildren() {
        return grandchildren;
    }
}
