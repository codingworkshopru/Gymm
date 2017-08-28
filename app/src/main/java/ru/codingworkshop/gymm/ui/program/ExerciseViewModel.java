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

public abstract class ExerciseViewModel<P extends Draftable, C> extends ViewModel {
    private BaseNode<P, C> node;

    public BaseNode<P, C> getNode() {
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
    protected abstract BaseNode<P, C> createNode();

    public LiveData<Boolean> load(long id) {
        node = createNode();
        NodeDataSource<P, C> dataSource = createDataSource(id);
        NodeLoader<P, C> loader = createLoader(dataSource);
        return loader.load();
    }

    protected abstract NodeDataSource<P,C> createDataSource(long id);
    protected abstract NodeLoader<P,C> createLoader(NodeDataSource<P,C> dataSource);

    public void save() {
        Saver saver = createSaver();
        saver.save();
    }

    protected abstract Saver createSaver();
}
