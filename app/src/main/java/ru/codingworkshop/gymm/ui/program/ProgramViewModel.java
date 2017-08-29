package ru.codingworkshop.gymm.ui.program;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ru.codingworkshop.gymm.data.entity.common.Draftable;
import ru.codingworkshop.gymm.data.tree.loader.NodeLoader;
import ru.codingworkshop.gymm.data.tree.loader.datasource.NodeDataSource;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.data.tree.saver.Saver;

/**
 * Created by Радик on 27.08.2017 as part of the Gymm project.
 */

public abstract class ProgramViewModel<P extends Draftable, C> extends ViewModel {
    private BaseNode<P, ? extends C> node;

    public ProgramViewModel() {
        this.node = createNode();
    }

    public BaseNode<P, ? extends C> getNode() {
        return node;
    }

    public void create() {
        P parent = createParent();
        parent.setDrafting(true);
        insertParent(parent);

        node = createNode();
        node.setParent(parent);
    }

    protected abstract P createParent();
    protected abstract void insertParent(P parent);
    protected abstract BaseNode<P, ? extends C> createNode();

    public LiveData<Boolean> load(long id) {
        node = createNode();
        NodeDataSource<P, ? extends C> dataSource = createDataSource(id);
        NodeLoader<P, ? extends C> loader = createLoader(dataSource);
        return loader.load();
    }

    protected abstract NodeDataSource<P, ? extends C> createDataSource(long id);
    protected abstract NodeLoader<P, ? extends C> createLoader(NodeDataSource<P, ? extends C> dataSource);

    public void save() {
        createSaver().save();
    }

    protected abstract Saver createSaver();
}
