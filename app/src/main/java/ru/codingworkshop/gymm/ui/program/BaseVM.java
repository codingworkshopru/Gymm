package ru.codingworkshop.gymm.ui.program;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import ru.codingworkshop.gymm.data.entity.common.Draftable;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.loader.NodeLoader;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.data.tree.saver.Saver;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;

/**
 * Created by Радик on 08.10.2017 as part of the Gymm project.
 */

public abstract class BaseVM<P extends Draftable & Model> {

    public LiveData<Boolean> create() {
        return Transformations.switchMap(getDrafting(), input -> {
            if (input == null) {
                init();
                return LiveDataUtil.getLive(true);
            } else {
                return load(input.getId());
            }
        });
    }

    public LiveData<Boolean> load(long id) {
        return getLoader(id).load();
    }

    public void save() {
        getNode().getParent().setDrafting(false);
        getSaver().save();
    }

    protected void init() {
        BaseNode<P, ?> node = getNode();
        P parent = createParent();
        parent.setDrafting(true);
        insertParent(parent);
        node.setParent(parent);
    }

    protected abstract void insertParent(P parent);

    protected abstract BaseNode<P, ?> getNode();

    protected abstract P createParent();

    protected abstract LiveData<P> getDrafting();

    protected abstract NodeLoader<?, ?> getLoader(long programTrainingId);

    protected abstract Saver getSaver();

}
